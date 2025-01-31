package com.nado.smartcare.config;

import com.nado.smartcare.employee.domain.dto.EmployeeDto;
import com.nado.smartcare.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    private final EmployeeService employeeService;

    public UserInfoInterceptor(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true; // 항상 true를 반환하여 요청 처리를 계속 진행
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // ModelAndView가 null이 아니고 view 이름이 있는 경우에만 처리
        if (modelAndView != null && modelAndView.getViewName() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof UserDetails) {
                try {
                    String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                    EmployeeDto employeeDto = employeeService.searchEmployee(username)
                            .orElseThrow(() -> new RuntimeException("Employee not found"));

                    // Model에 사용자 정보와 로그인 상태 추가
                    modelAndView.addObject("employee", employeeDto);
                    modelAndView.addObject("loginStatus", "authenticated");
                } catch (Exception e) {
                    modelAndView.addObject("loginStatus", "error");
                    modelAndView.addObject("errorMessage", "사용자 정보를 불러올 수 없습니다.");
                }
            } else {
                modelAndView.addObject("loginStatus", "anonymous");
            }
        }
    }

    /*
    * Authenticaiton 객체를 통한 로그인 사용자 정보 획득 방법
    *
    * @GetMapping("/some-endpoint")
    public String someMethod(Authentication authentication) {
    // UserDetails를 통해 사용자 정보 가져오기
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String username = userDetails.getUsername();

    // 이제 이 username으로 필요한 정보를 조회
    EmployeeDto employee = employeeService.searchEmployee(username)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // employee 객체를 통해 필요한 정보 사용
    String employeeName = employee.employeeName();
    Long employeeNo = employee.employeeNo();

    return "some-view";
    }
    * */
}
