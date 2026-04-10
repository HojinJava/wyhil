package com.hnix.sd.common.department.dao;

import com.hnix.sd.common.department.dto.DepartmentDto;
import com.hnix.sd.common.department.dto.DeptTreeDto;
import com.hnix.sd.common.department.dto.DepartmentStructureDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface DepartmentDao {

    DepartmentDto findByDeptCdEntity(@Param("deptCd") String deptCd);

    default Optional<DepartmentDto> findById(String deptCd) {
        return Optional.ofNullable(findByDeptCdEntity(deptCd));
    }

    default DepartmentDto findByDeptCd(String deptCd) {
        return findByDeptCdEntity(deptCd);
    }

    boolean existsByDeptCd(@Param("deptCd") String deptCd);

    default boolean existsById(String deptCd) {
        return existsByDeptCd(deptCd);
    }

    String findCompanyTypeCdByDeptCdString(@Param("deptCd") String deptCd);

    default Optional<Character> findCompanyTypeCdByDeptCd(String deptCd) {
        String code = findCompanyTypeCdByDeptCdString(deptCd);
        return (code != null && !code.isEmpty()) ? Optional.of(code.charAt(0)) : Optional.empty();
    }

    void insertDepartment(DepartmentDto department);

    void updateDepartment(DepartmentDto department);

    default DepartmentDto save(DepartmentDto department) {
        DepartmentDto existing = findByDeptCdEntity(department.getDeptCd());
        if (existing == null) {
            insertDepartment(department);
        } else {
            updateDepartment(department);
        }
        return department;
    }

    void deleteByDeptCd(@Param("deptCd") String deptCd);

    default void deleteById(String deptCd) {
        deleteByDeptCd(deptCd);
    }

    void deleteByDeptCds(@Param("deptCds") List<String> deptCds);

    Set<String> findBelongsUserByDept(@Param("deptCodes") List<String> deptCodes);

    Set<String> findExistingCodes(@Param("deptCodes") List<String> deptCodes);

    boolean existsByDeptTypeCd(@Param("deptTypeCd") Character deptTypeCd);

    boolean existsByCompanyTypeCd(@Param("companyTypeCd") Character companyTypeCd);

    List<DeptTreeDto> findTreeByDeptCd(@Param("deptCd") String deptCd);

    List<Map<String, Object>> findAllPrntDeptNm();

    List<Map<String, Object>> findAllPrntDeptNmByDeptCd(@Param("deptCd") List<String> deptCd);

    List<DeptTreeDto> findTreeByCompanyTypeCd(@Param("companyTypeCd") Character companyTypeCd);

    Map<String, Object> findByDeptFromCodeEntity(@Param("deptCd") String deptCd);

    default Optional<Object[]> findByDeptFromCode(String deptCd) {
        Map<String, Object> map = findByDeptFromCodeEntity(deptCd);
        if (map == null) return Optional.empty();
        return Optional.of(new Object[]{map.get("company"), mapToDepartment(map)});
    }

    List<Map<String, Object>> findDeptWithCompanyNameEntity();

    default List<Object[]> findDeptWithCompanyName() {
        return findDeptWithCompanyNameEntity().stream()
                .map(map -> new Object[]{map.get("company"), mapToDepartment(map)})
                .toList();
    }

    List<DepartmentDto> findByCompany();

    List<DepartmentDto> findByDeptWithCompany(@Param("deptCd") String deptCd);

    List<Map<String, Object>> findAllDepartmentEntity(@Param("compTypeCd") Character compTypeCd);

    default List<Object[]> findAllDepartment(Character compTypeCd) {
        return findAllDepartmentEntity(compTypeCd).stream()
                .map(map -> new Object[]{map.get("company"), mapToDepartment(map)})
                .toList();
    }

    List<Map<String, Object>> findByDeptFromKeywordEntity(@Param("keyword") String keyword);

    default List<Object[]> findByDeptFromKeyword(String keyword) {
        return findByDeptFromKeywordEntity(keyword).stream()
                .map(map -> new Object[]{map.get("company"), mapToDepartment(map)})
                .toList();
    }

    List<DepartmentStructureDto> findByDeptFromCompanyCd(@Param("companyCd") String companyCd);

    List<DepartmentStructureDto> findByDeptTypeNotCompany();

    List<DepartmentStructureDto> findStructureByDeptTypeCd(@Param("deptTypeCd") Character deptTypeCd);

    default List<DepartmentStructureDto> findByDeptTypeCd(Character deptTypeCd) {
        return findStructureByDeptTypeCd(deptTypeCd);
    }

    String findByCompanyCdFromDeptCdString(@Param("deptCd") String deptCd);

    default Optional<String> findByCompanyCdFromDeptCd(String deptCd) {
        return Optional.ofNullable(findByCompanyCdFromDeptCdString(deptCd));
    }

    default DepartmentDto mapToDepartment(Map<String, Object> map) {
        if (map == null) return null;
        DepartmentDto dept = new DepartmentDto();

        // String 타입 필드들 (기존의 삼항 연산자로 null 처리가 안전하게 되어 있음)
        Object deptCd = map.containsKey("dept_cd") ? map.get("dept_cd") : map.get("deptCd");
        dept.setDeptCd(deptCd != null ? deptCd.toString() : null);

        Object deptNm = map.containsKey("dept_nm") ? map.get("dept_nm") : map.get("deptNm");
        dept.setDeptNm(deptNm != null ? deptNm.toString() : null);

        // [수정됨] char 타입 변환 필드 - null 체크 추가
        Object deptTypeCd = map.containsKey("dept_type_cd") ? map.get("dept_type_cd") : map.get("deptTypeCd");
        if (deptTypeCd != null) {
            dept.setDeptTypeCd(deptTypeCd instanceof String && !((String) deptTypeCd).isEmpty()
                    ? ((String) deptTypeCd).charAt(0)
                    : (Character) deptTypeCd);
        }

        Object prntDeptCd = map.containsKey("prnt_dept_cd") ? map.get("prnt_dept_cd") : map.get("prntDeptCd");
        dept.setPrntDeptCd(prntDeptCd != null ? prntDeptCd.toString() : null);

        Object compClassCd = map.containsKey("comp_class_cd") ? map.get("comp_class_cd") : map.get("compClassCd");
        dept.setCompClassCd(compClassCd != null ? compClassCd.toString() : null);

        // [수정됨] 이전 단계에서 수정한 필드
        Object companyTypeCd = map.containsKey("comp_type_cd") ? map.get("comp_type_cd") : map.get("companyTypeCd");
        if (companyTypeCd != null) {
            dept.setCompanyTypeCd(
                    companyTypeCd instanceof String && !((String) companyTypeCd).isEmpty()
                            ? ((String) companyTypeCd).charAt(0)
                            : (Character) companyTypeCd
            );
        }

        Object deptDesc = map.containsKey("dept_desc") ? map.get("dept_desc") : map.get("deptDesc");
        dept.setDeptDesc(deptDesc != null ? deptDesc.toString() : null);

        // [수정됨] char 타입 변환 필드 - null 체크 추가
        Object useYn = map.containsKey("use_yn") ? map.get("use_yn") : map.get("useYn");
        if (useYn != null) {
            dept.setUseYn(useYn instanceof String && !((String) useYn).isEmpty()
                    ? ((String) useYn).charAt(0)
                    : (Character) useYn);
        }

        return dept;
    }
}
