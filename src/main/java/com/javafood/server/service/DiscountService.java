package com.javafood.server.service;

import com.javafood.server.dto.request.DiscountRequest;
import com.javafood.server.dto.response.DiscountResponse;
import com.javafood.server.entity.DiscountEntity;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.mapper.DiscountMapper;
import com.javafood.server.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountService {
    private static final String authorizeAdmin = "hasAuthority('SCOPE_ADMIN')";
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private DiscountMapper discountMapper;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public DiscountResponse createDiscount(DiscountRequest discountRequest) {
        DiscountEntity discountEntity = discountMapper.toDiscountEntity(discountRequest);
        discountRepository.save(discountEntity);
        return discountMapper.toDiscountResponse(discountEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public DiscountResponse updateDiscount(Integer discountID, DiscountRequest discountRequest) {
        DiscountEntity discountEntity = discountRepository.findById(discountID).orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTS_DATA));
        if(discountRepository.existsByCode(discountRequest.getCode())) throw new AppException(ErrorCode.EXISTS_DATA);

        discountEntity.setCode(discountRequest.getCode());
        discountEntity.setDiscountValue(discountRequest.getDiscountValue());
        discountEntity.setStartDate(discountRequest.getStartDate());
        discountEntity.setEndDate(discountRequest.getEndDate());
        discountEntity.setActive(discountRequest.getActive());

        discountRepository.save(discountEntity);
        return discountMapper.toDiscountResponse(discountEntity);
    }

    @PreAuthorize(authorizeAdmin)
    public List<DiscountResponse> getDiscount() {
        return discountRepository.findAll().stream().map(discountMapper::toDiscountResponse).collect(Collectors.toList());
    }

    @PreAuthorize(authorizeAdmin)
    public void deleteDiscount(Integer discountID) {
        if(!discountRepository.existsById(discountID)) throw new AppException(ErrorCode.NOT_EXISTS_DATA);
        discountRepository.deleteById(discountID);
    }

}
