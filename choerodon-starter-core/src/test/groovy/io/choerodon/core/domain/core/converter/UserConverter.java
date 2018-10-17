package io.choerodon.core.domain.core.converter;

import io.choerodon.core.api.dto.UserDTO;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.core.domain.core.entity.UserE;
import org.springframework.beans.BeanUtils;

/**
 * @author dengyouquan
 **/
public class UserConverter implements ConvertorI<UserE, Object, UserDTO> {
    @Override
    public UserE dtoToEntity(UserDTO dto) {
        UserE userE = new UserE();
        BeanUtils.copyProperties(dto, userE);
        return userE;
    }

    @Override
    public UserDTO entityToDto(UserE entity) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(entity, userDTO);
        return userDTO;
    }
}
