package com.sfy.educms.controller;

import com.sfy.commonutils.R;
import com.sfy.educms.entity.CrmBanner;
import com.sfy.educms.service.CrmBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 后台banner管理接口
 * </p>
 *
 * @author testjava
 * @since 2023-12-05
 */
@RestController
@RequestMapping("/educms/banneradmin")

public class BannerAdminController {

    @Autowired
    private CrmBannerService bannerService;

    //查询所有banner
    @GetMapping("getAllBanner")
    public R pageBanner() {
        List<CrmBanner> bannerList = bannerService.list(null);
        return R.ok().data("bannerList", bannerList);
    }

    //添加banner
    @PostMapping("addBanner")
    public R addBanner(@RequestBody CrmBanner crmBanner) {
        Integer sort = 0;
        crmBanner.setSort(sort++);
        bannerService.save(crmBanner);
        return R.ok();
    }

    //根据id查询banner
    @GetMapping("getbanner/{id}")
    public R get(@PathVariable String id) {
        CrmBanner banner = bannerService.getById(id);
        return R.ok().data("item", banner);
    }

    //修改banner
    @PostMapping("updateBanner")
    public R updateById(@RequestBody CrmBanner crmBanner) {
        bannerService.updateById(crmBanner);
        return R.ok();
    }

    //删除banner
    @DeleteMapping("delete/{id}")
    public R remove(@PathVariable String id) {
        bannerService.removeById(id);
        return R.ok();
    }

}

