package com.hnix.sd.common.user.service;

import com.hnix.sd.common.user.dao.UserDao;
import com.hnix.sd.common.user.dto.UserDto;
import com.hnix.sd.common.user.dto.ChangeUserInfoDto;
import com.hnix.sd.common.user.dto.UserExcelDto;
import com.hnix.sd.common.user.dto.UserGridDto;
import com.hnix.sd.common.user.dto.UserRegistDto;
import com.hnix.sd.core.utils.MemberUtil;
import com.hnix.sd.common.excel.dto.ExcelField;
import com.hnix.sd.common.excel.util.ExcelFormat.ALIGN;
import com.hnix.sd.common.excel.util.WorkCellStyle;
import com.hnix.sd.common.excel.util.WorkSheetLayout;
import com.hnix.sd.common.auth.user.service.UserAuthService;
import com.hnix.sd.common.department.dao.DepartmentDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserExcelService {

    private final UserPageService userPageService;
    private final UserService userService;
    private final UserLogService userLogService;
    private final UserDao userDao;
    private final DepartmentDao departmentDao;
    private final MemberUtil memberUtil;
    private final UserAuthService userAuthService;
    private final UserHistoryService userHistoryService;

    public SXSSFWorkbook exportUserExcel(UserGridDto searchDto) {
		List<UserExcelDto> excelDataList;

        String deptCd = searchDto.getDeptCd() != null ? searchDto.getDeptCd() : "";
        String userNm = searchDto.getUserNm() != null ? searchDto.getUserNm() : "";
        String userEmail = searchDto.getUserEmail() != null ? searchDto.getUserEmail() : "";
	
		try {
			excelDataList = userPageService.getAllUsers(deptCd, userNm, userEmail)
					.stream()
					.map(u -> {
                        UserExcelDto dto = new UserExcelDto();
                        dto.setCompanyNm(u.getCompanyNm() == null ? "" : u.getCompanyNm());
                        dto.setDepartmentNm(u.getDepartmentNm() == null ? "" : u.getDepartmentNm());
                        dto.setUserDeptNm(u.getUserDeptNm() == null ? "" : u.getUserDeptNm());
                        dto.setDeptCd(u.getDeptCd() == null ? "" : u.getDeptCd());
                        dto.setUserPositionNm(u.getUserPositionNm() == null ? "" : u.getUserPositionNm());
                        dto.setUserNm(u.getUserNm() == null ? "" : u.getUserNm());
                        dto.setUserId(u.getUserId() == null ? "" : u.getUserId());
                        dto.setUserEmail(u.getUserEmail() == null ? "" : u.getUserEmail());
                        dto.setUserPhoneMobile(u.getUserPhoneMobile() == null ? "" : u.getUserPhoneMobile());
                        dto.setUserPhoneOffice(u.getUserPhoneOffice() == null ? "" : u.getUserPhoneOffice());
                        dto.setRemark(u.getRemark() == null ? "" : u.getRemark());
                        return dto;
                    })
                    .collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Error retrieving user data for excel", e);
			excelDataList = new ArrayList<>();
		}
	
		List<ExcelField> fields = new ArrayList<>();
		fields.add(new ExcelField("companyNm", "Company", ALIGN.LEFT, 6000));
		fields.add(new ExcelField("departmentNm", "Business Division", ALIGN.LEFT, 6000));
		fields.add(new ExcelField("userDeptNm", "Department", ALIGN.LEFT, 6000));
		fields.add(new ExcelField("deptCd", "Dept Code", ALIGN.CENTER, 8000));
		fields.add(new ExcelField("userPositionNm", "Position", ALIGN.LEFT, 4000));
		fields.add(new ExcelField("userNm", "Name", ALIGN.LEFT, 4000));
        fields.add(new ExcelField("userId", "User ID", ALIGN.LEFT, 4000));
		fields.add(new ExcelField("userEmail", "Email", ALIGN.LEFT, 8000));
		fields.add(new ExcelField("userPhoneMobile", "Mobile", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("userPhoneOffice", "Office Phone", ALIGN.LEFT, 5000));
        fields.add(new ExcelField("remark", "Remark", ALIGN.LEFT, 5000));
         
        userLogService.saveUserListLog(
            UserLogService.ACCESS_TYPE_MENU,
            "common-dept-user",
            deptCd,
            userNm,
            userEmail
        );
		return createUserExcelFile(excelDataList, fields);
	}

    private SXSSFWorkbook createUserExcelFile(List<UserExcelDto> results, List<ExcelField> fields) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = WorkSheetLayout.createSheetLayout(workbook, "User Management", fields);

        Font bodyFont = WorkCellStyle.setCellFont(workbook.createFont(), 230, false);
        CellStyle bodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.CENTER, true, false, null);
        CellStyle alignBodyCellStyle = WorkCellStyle.setCellStyle(workbook.createCellStyle(), bodyFont, ALIGN.LEFT, true, false, null);

        int startRowIndex = 4;

        for (UserExcelDto dto : results) {
            SXSSFRow row = sheet.createRow(startRowIndex++);
            List<String> rowData = getUserExcelFieldValues(dto);

            for (int i = 0; i < fields.size(); i++) {
                ExcelField field = fields.get(i);
                String val = rowData.get(i);
                SXSSFCell cell = row.createCell(i + 1);
                cell.setCellValue(val == null ? "" : val);
                cell.setCellStyle(field.getAlign() == ALIGN.CENTER ? bodyCellStyle : alignBodyCellStyle);
            }
        }

        if (!results.isEmpty()) {
            int filterFirstRow = 4;
            int filterLastRow = results.size() - 1 + filterFirstRow;
            sheet.setAutoFilter(CellRangeAddress.valueOf(String.format("B%d:K%d", filterFirstRow, filterLastRow)));
        }

        return workbook;
    }

    private List<String> getUserExcelFieldValues(UserExcelDto dto) {
        return List.of(
            dto.getCompanyNm() == null ? "" : dto.getCompanyNm(),
            dto.getDepartmentNm() == null ? "" : dto.getDepartmentNm(),
            dto.getUserDeptNm() == null ? "" : dto.getUserDeptNm(),
            dto.getDeptCd() == null ? "" : dto.getDeptCd(),
            dto.getUserPositionNm() == null ? "" : dto.getUserPositionNm(),
            dto.getUserNm() == null ? "" : dto.getUserNm(),
            dto.getUserId() == null ? "" : dto.getUserId(),
            dto.getUserEmail() == null ? "" : dto.getUserEmail(),
            dto.getUserPhoneMobile() == null ? "" : dto.getUserPhoneMobile(),
            dto.getUserPhoneOffice() == null ? "" : dto.getUserPhoneOffice(),
            dto.getRemark() == null ? "" : dto.getRemark()
        );
    }

    public List<UserExcelDto> parseUserExcel(MultipartFile file) {
        List<UserExcelDto> dtoList = new ArrayList<>();

        try (InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowIndex = 0;

            while (rows.hasNext()) {
                Row row = rows.next();

                if (rowIndex++ < 2) continue;

                UserExcelDto dto = new UserExcelDto();

                dto.setCompanyNm(getCellValue(row, 1));
                dto.setDepartmentNm(getCellValue(row, 2));
                dto.setUserDeptNm(getCellValue(row, 3));
                dto.setDeptCd(getCellValue(row, 4).trim());
                dto.setUserPositionNm(getCellValue(row, 5));
                dto.setUserNm(getCellValue(row, 6));
                dto.setUserId(getCellValue(row, 7));
                dto.setUserEmail(getCellValue(row, 8));
                dto.setUserPhoneMobile(getCellValue(row, 9));
                dto.setUserPhoneOffice(getCellValue(row, 10));
                dto.setRemark(getCellValue(row, 11));

                dto.setErrorMsg(dto.validate(departmentDao::existsByDeptCd));

                if (userDao.existsByUserId(dto.getUserId())) {
                    dto.setNewRow(false);
                    dto.setUpdateRow(true);
                } else {
                    dto.setNewRow(true);
                    dto.setUpdateRow(false);
                }
                dtoList.add(dto);
            }

        } catch (Exception e) {
            log.error("Excel parsing error", e);
            throw new RuntimeException("Excel parsing failed: " + e.getMessage(), e);
        }

        return dtoList;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    public List<String> storeUserList(List<UserExcelDto> excelDtoList) {
        List<String> failedList = new ArrayList<>();
        
        for (UserExcelDto excel : excelDtoList) {
            try {
                if (excel.isNewRow()) {
                    UserDto user = new UserDto();
                    user.setDeptCd(excel.getDeptCd());
                    user.setUserNm(excel.getUserNm());
                    user.setUserEmail(excel.getUserEmail());
                    user.setUserId(excel.getUserId());
                    user.setUserDeptNm(excel.getUserDeptNm());
                    user.setUserPositionNm(excel.getUserPositionNm());
                    user.setUserPhoneMobile(excel.getUserPhoneMobile());
                    user.setUserPhoneOffice(excel.getUserPhoneOffice());
                    user.setRemark(excel.getRemark());

                    user.setUserCertYn('N');
                    user.setUserConsentYn('N');
                    user.setDeleteYn('N');

                    user.setRegId(memberUtil.getUserId());
                    user.setRegDt(LocalDateTime.now());
                    
                    Character companyTypeCd = departmentDao.findCompanyTypeCdByDeptCd(excel.getDeptCd())
                    .orElse(null);

                    UserRegistDto registDto = new UserRegistDto();
                    registDto.setUserId(excel.getUserId());
                    registDto.setDeptCd(excel.getDeptCd());
                    registDto.setUserNm(excel.getUserNm());
                    registDto.setUserDeptNm(excel.getUserDeptNm());
                    registDto.setUserEmail(excel.getUserEmail());
                    registDto.setUserPositionNm(excel.getUserPositionNm());
                    registDto.setUserPhoneMobile(excel.getUserPhoneMobile());
                    registDto.setUserPhoneOffice(excel.getUserPhoneOffice());
                    registDto.setRemark(excel.getRemark());
                    registDto.setRegId(memberUtil.getUserId());

                    userDao.save(user);
                    userHistoryService.createUserHistory(user, registDto);

                    if (companyTypeCd != null) {
                        userAuthService.storeUserDefaultAuth(
                            excel.getUserId(), 
                            memberUtil.getUserId(), 
                            String.valueOf(companyTypeCd)
                        );
                    }
                    
                } else if (excel.isUpdateRow()) {
                    ChangeUserInfoDto updateDto = new ChangeUserInfoDto();
                    updateDto.setUserId(excel.getUserId());
                    updateDto.setUserEmail(excel.getUserEmail());
                    updateDto.setDeptCd(excel.getDeptCd());
                    updateDto.setUserDeptNm(excel.getUserDeptNm());
                    updateDto.setUserPositionNm(excel.getUserPositionNm());
                    updateDto.setUserPhoneMobile(excel.getUserPhoneMobile());
                    updateDto.setUserPhoneOffice(excel.getUserPhoneOffice());
                    updateDto.setRemark(excel.getRemark());
                    updateDto.setModId(memberUtil.getUserId());
                    
                    Character companyTypeCd = departmentDao.findCompanyTypeCdByDeptCd(excel.getDeptCd())
                    .orElse(null);
                    if (companyTypeCd != null) {
                        updateDto.setCompanyTypeCd(String.valueOf(companyTypeCd));
                    }
                    
                    userService.updateUserInfo(updateDto);
                }
                
            } catch (Exception e) {
                log.error("User save failed: {}", excel.getUserId(), e);
                failedList.add(excel.getUserId());
            }
        }
        
        return failedList;
    }
}
