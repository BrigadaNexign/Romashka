package rom.hrs.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import rom.hrs.dto.*;
import rom.hrs.entity.*;
import rom.hrs.exception.NoIntervalsFoundException;
import rom.hrs.exception.NoTariffFoundException;
import rom.hrs.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TariffService {
    private static final Logger logger = LoggerFactory.getLogger(TariffService.class);
    private final TariffRepository tariffRepository;
    private final SubscriberTariffRepository subscriberTariffRepository;
    private final TariffIntervalRepository tariffIntervalRepository;
    private final CallPricingRepository callPricingRepository;
    private final CallPricingService callPricingService;
    private final ParameterRepository parameterRepository;
    private final TariffParameterRepository tariffParameterRepository;
    private final TariffParamService tariffParamService;

    public Tariff findTariffById(long tariffId) throws NoTariffFoundException {
        logger.debug("Finding tariff with ID: {}", tariffId);
        return tariffRepository.findById(tariffId)
                .orElseThrow(() -> new NoTariffFoundException(tariffId));
    }

    @Transactional
    public TariffResponse findTariffResponseById(long tariffId) throws NoTariffFoundException {
        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new NoTariffFoundException(tariffId));
        return mapToResponseSafe(tariff);
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> getAllTariffs(String sortBy) {
        logger.debug("Retrieving all tariffs with sorting: {}", sortBy);

        Sort sort = sortBy != null ? Sort.by(sortBy) : Sort.unsorted();
        List<Tariff> tariffs = tariffRepository.findAll(sort);

        return tariffs.stream()
                .map(this::mapToResponseSafe)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TariffResponse getTariffByMsisdn(String msisdn){
        logger.debug("Retrieving tariff for MSISDN: {}", msisdn);
        SubscriberTariff subscriberTariff = subscriberTariffRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff not found for MSISDN"));
        Tariff tariff = tariffRepository.findById((long) subscriberTariff.getTariffId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff not found"));

        return mapToResponseSafe(tariff);
    }

    @Transactional
    public void changeTariff(String msisdn, ChangeTariffRequest request) {
        logger.debug("Changing tariff for MSISDN: {} to tariff ID: {}", msisdn, request.tariffId());
        tariffRepository.findById(request.tariffId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff not found"));

        SubscriberTariff subscriberTariff = subscriberTariffRepository.findByMsisdn(msisdn)
                .orElseGet(() -> SubscriberTariff.builder()
                        .msisdn(msisdn)
                        .build());
        subscriberTariff.setTariffId(request.tariffId().intValue());
        subscriberTariffRepository.save(subscriberTariff);
    }

    @Transactional
    public void createTariff(CreateTariffRequest request) {
        logger.debug("Creating new tariff with name: {}", request.name());
        if (tariffRepository.findByName(request.name()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tariff with name " + request.name() + " already exists");
        }

        Tariff tariff = Tariff.builder()
                .name(request.name())
                .description(request.description())
                .build();
        tariff = tariffRepository.save(tariff);

        // Save tariff interval
        TariffInterval interval = TariffInterval.builder()
                .id(new TariffIntervalId(tariff.getId(), request.intervalDays()))
                .price(BigDecimal.valueOf(request.price()))
                .tariff(tariff)
                .build();
        tariffIntervalRepository.save(interval);

        // Save call pricing
        if (request.callPrices() != null) {
            for (CallPriceDto callPrice : request.callPrices()) {
                CallPricing pricing = CallPricing.builder()
                        .id(new CallPricingId(tariff.getId(), callPrice.getCallType()))
                        .costPerMin(BigDecimal.valueOf(callPrice.getPricePerMinute()))
                        .tariff(tariff)
                        .build();
                callPricingRepository.save(pricing);
            }
        }

        // Save tariff parameters
        if (request.params() != null) {
            for (TariffParamDto param : request.params()) {
                Parameter parameter = parameterRepository.findByName(param.name())
                        .orElseGet(() -> {
                            Parameter newParam = Parameter.builder()
                                    .name(param.name())
                                    .description(param.description())
                                    .units(param.units())
                                    .build();
                            return parameterRepository.save(newParam);
                        });

                TariffParameter tariffParam = TariffParameter.builder()
                        .id(new TariffParameter.TariffParameterId(tariff.getId(), parameter.getId()))
                        .value(BigDecimal.valueOf(param.value() != null ? param.value() : 0.0))
                        .tariff(tariff)
                        .parameter(parameter)
                        .build();
                tariffParameterRepository.save(tariffParam);
            }
        }
    }

    @Transactional
    public void deleteTariff(Long id) {
        tariffRepository.deleteById(id);
    }

    private TariffResponse mapToResponseSafe(Tariff tariff) {
        try {
            return mapToResponse(tariff);
        } catch (NoIntervalsFoundException e) {
            logger.warn("No intervals found for tariff ID: {}. Skipping this tariff.", tariff.getId());
        }
        return null;
    }

    private TariffResponse mapToResponse(Tariff tariff) throws NoIntervalsFoundException {
        List<TariffInterval> tariffIntervalList = tariffIntervalRepository.findAllByTariffId(tariff.getId());

        if (tariffIntervalList.isEmpty()) throw new NoIntervalsFoundException(tariff.getId());
        if (tariffIntervalList.size() > 1) logger.warn("Got multiple tariff intervals, using first");

        TariffInterval tariffInterval = tariffIntervalList.get(0);

        TariffResponse response = new TariffResponse(
                tariff.getId(),
                tariff.getName(),
                tariff.getDescription(),
                tariffInterval.getId().getInterval(),
                tariffInterval.getPrice().doubleValue(),
                tariff.getType(),
                callPricingService.findListOfDtoById(tariff.getId()),
                tariffParamService.findTariffResponseByTariffId(tariff.getId())
        );
        return response;
    }
}