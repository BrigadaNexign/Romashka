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
import java.util.stream.Collectors;

/**
 * Сервис для работы с тарифами.
 * Предоставляет CRUD операции и методы поиска тарифов.
 */
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
        return mapToResponse(tariff);
    }

    @Transactional(readOnly = true)
    public List<TariffResponse> getAllTariffs(String sortBy) {
        logger.debug("Retrieving all tariffs with sorting: {}", sortBy);

        Sort sort = sortBy != null ? Sort.by(sortBy) : Sort.unsorted();
        List<Tariff> tariffs = tariffRepository.findAll(sort);

        logger.info("Got tariffs {}", tariffs);

        return tariffs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TariffResponse getTariffByMsisdn(String msisdn){
        logger.debug("Retrieving tariff for MSISDN: {}", msisdn);
        SubscriberTariff subscriberTariff = subscriberTariffRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff not found for MSISDN"));
        Tariff tariff = tariffRepository.findById((long) subscriberTariff.getTariffId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff not found"));

        return mapToResponse(tariff);
    }

    @Transactional
    public TariffResponse createTariff(CreateTariffRequest request) {
        logger.debug("Creating new tariff with name: {}", request.name());
        if (tariffRepository.findByName(request.name()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tariff with name " + request.name() + " already exists");
        }

        Tariff tariff = Tariff.builder()
                .name(request.name())
                .description(request.description())
                .build();
        tariff = tariffRepository.save(tariff);

        TariffInterval interval = TariffInterval.builder()
                .id(new TariffIntervalId(tariff.getId(), request.intervalDays()))
                .price(BigDecimal.valueOf(request.price()))
                .tariff(tariff)
                .build();
        tariffIntervalRepository.save(interval);

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

        return mapToResponse(tariff);
    }

    @Transactional
    public void deleteTariff(Long id) {
        tariffRepository.deleteById(id);
    }

    private TariffResponse mapToResponse(Tariff tariff) {
        List<TariffInterval> tariffIntervalList = tariffIntervalRepository.findAllByTariffId(tariff.getId());

        TariffInterval tariffInterval = null;

        if (tariffIntervalList.size() > 1) logger.warn("Got multiple tariff intervals, using first");
        if (tariffIntervalList.size() == 1) tariffInterval = tariffIntervalList.get(0);

        return new TariffResponse(
                tariff.getId(),
                tariff.getName(),
                tariff.getDescription(),
                tariffInterval != null ? tariffInterval.getId().getInterval() : null,
                tariffInterval != null ? tariffInterval.getPrice().doubleValue(): null,
                tariff.getType(),
                callPricingService.findListOfDtoById(tariff.getId()),
                tariffParamService.findTariffResponseByTariffId(tariff.getId())
        );
    }
}