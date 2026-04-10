package com.hnix.sd.common.user.dao;

import com.hnix.sd.common.user.dto.UserEmailDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserEmailDao {

    UserEmailDto findByUserIdEntity(@Param("userId") String userId);

    default Optional<UserEmailDto> findByUserId(String userId) {
        return Optional.ofNullable(findByUserIdEntity(userId));
    }

    List<UserEmailDto> findByIdxRangeAndSendYnN(@Param("fromIdx") Integer fromIdx, @Param("toIdx") Integer toIdx);

    void insertUserEmail(UserEmailDto userEmail);

    void updateUserEmail(UserEmailDto userEmail);

    default void save(UserEmailDto userEmail) {
        if (userEmail.getUserId() != null && findByUserIdEntity(userEmail.getUserId()) != null) {
            updateUserEmail(userEmail);
        } else {
            insertUserEmail(userEmail);
        }
    }
}
