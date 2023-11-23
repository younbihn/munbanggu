package com.zerobase.munbanggu.community.converter;

import com.zerobase.munbanggu.user.type.CommunityCategoty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CommunityCategoryConverter implements AttributeConverter<CommunityCategoty, String> {

    @Override
    public String convertToDatabaseColumn(CommunityCategoty category) {
        if (category == null) {
            return null;
        }
        return category.getLabel();
    }

    @Override
    public CommunityCategoty convertToEntityAttribute(String label) {
        if (label == null) {
            return null;
        }

        for (CommunityCategoty category : CommunityCategoty.values()) {
            if (category.getLabel().equals(label)) {
                return category;
            }
        }

        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
