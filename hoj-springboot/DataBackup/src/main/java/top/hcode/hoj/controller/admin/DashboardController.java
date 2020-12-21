package top.hcode.hoj.controller.admin;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.dao.ContestMapper;
import top.hcode.hoj.dao.JudgeMapper;
import top.hcode.hoj.dao.SessionMapper;
import top.hcode.hoj.pojo.entity.Session;
import top.hcode.hoj.service.UserInfoService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: Himit_ZH
 * @Date: 2020/12/6 15:10
 * @Description:
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiresAuthentication
@RequiresRoles(value = {"root","admin"},logical = Logical.OR)
public class DashboardController {

    @Autowired
    private ContestMapper contestDao;

    @Autowired
    private JudgeMapper judgeDao;

    @Autowired
    private UserInfoService userInfoDao;

    @Autowired
    private SessionMapper sessionDao;

    @PostMapping("/get-sessions")
    public CommonResult getSessions(HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        QueryWrapper<Session> wrapper = new QueryWrapper<Session>().eq("uid",userId).orderByDesc("gmt_create");
        List<Session> sessionList = sessionDao.selectList(wrapper);
        if (sessionList.size()>1){
            return CommonResult.successResponse(sessionList.get(1));
        }else{
            return CommonResult.successResponse(sessionList.get(0));
        }
    }

    @GetMapping("/get-dashboard-info")
    public CommonResult getDashboardInfo(){
        int userNum = userInfoDao.count();
        int recentContestNum = contestDao.getWithinNext14DaysContests().size();
        int todayJudgeNum = judgeDao.getTodayJudgeNum();
        return CommonResult.successResponse(MapUtil.builder()
                        .put("userNum",userNum)
                        .put("recentContestNum",recentContestNum)
                        .put("todayJudgeNum",todayJudgeNum).map(),
                "查询成功");
    }
}