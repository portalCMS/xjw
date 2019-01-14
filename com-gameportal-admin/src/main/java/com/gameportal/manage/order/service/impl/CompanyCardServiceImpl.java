package com.gameportal.manage.order.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gameportal.manage.order.dao.CCAndGroupDao;
import com.gameportal.manage.order.dao.CCGroupxAndUserDao;
import com.gameportal.manage.order.dao.CompanyCardDao;
import com.gameportal.manage.order.model.CompanyCard;
import com.gameportal.manage.order.service.ICompanyCardService;
import com.gameportal.manage.system.model.SystemUser;
import com.gameportal.manage.user.dao.UserInfoDao;
import com.gameportal.manage.user.model.UserInfo;

@Service("companyCardServiceImpl")
public class CompanyCardServiceImpl implements ICompanyCardService {

	private static final Logger logger = Logger
			.getLogger(CompanyCardServiceImpl.class);

	@Resource
	private CompanyCardDao companyCardDao = null;
	@Resource
	private CCAndGroupDao cCAndGroupDao = null;
	@Resource
	private CCGroupxAndUserDao cCGroupxAndUserDao = null;
	@Resource
	private UserInfoDao userInfoDao = null;

	public CompanyCardServiceImpl() {
		super();
	}

	@Override
	public boolean saveOrUpdateCompanyCard(CompanyCard card) {
		return companyCardDao.saveOrUpdate(card);
	}

	@Override
	public boolean deleteCompanyCard(Long ccid) {
		return companyCardDao.delete(ccid);
	}

	@Override
	public boolean updateStatus(Long ccid, Integer status, SystemUser sysuser) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ccid", ccid);
		params.put("status", status);
		params.put("updateuserid", sysuser.getUserId());
		params.put("updateusername", sysuser.getRealName());
		params.put("updatetime", new Timestamp(new Date().getTime()));
		return companyCardDao.updateStatus(params);
	}

	@Override
	public Long queryCompanyCardCount(Map<String, Object> params) {
		return companyCardDao.getRecordCount(params);
	}

	@Override
	public List<CompanyCard> queryCompanyCard(Map<String, Object> params,
			Integer startNo, Integer pageSize) {
		if (!StringUtils.isNotBlank(ObjectUtils.toString(startNo))
				|| !StringUtils.isNotBlank(ObjectUtils.toString(pageSize))) {
			startNo = 0;
			pageSize = 30;
		}
		params.put("limit", true);
		params.put("thisPage", startNo);
		params.put("pageSize", pageSize);
		return companyCardDao.getList(params);

	}

	@Override
	public List<CompanyCard> queryByCcgid(Long ccgid) {
		return companyCardDao.queryByCcgid(ccgid);
	}

	@Override
	public CompanyCard getByUuid(Long uiid) {
		CompanyCard cc = null;
		Random random = new Random();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uiid", uiid);
		// 优先查询自定义分组
		List<CompanyCard> ccList = companyCardDao.queryByUiidInCcgx(params);
		if (null != ccList && ccList.size() > 0) {
			cc = ccList.get(random.nextInt(ccList.size()));
		}
		if (null == cc) {
			// 根据该会员的星级查询对应关联公司银行卡
			UserInfo user = (UserInfo) userInfoDao.findById(uiid);
			if (null != user && null != user.getGrade()) {
				ccList = companyCardDao.queryByGrade(user.getGrade());
				if (null != ccList && ccList.size() > 0) {
					cc = ccList.get(random.nextInt(ccList.size()));
				}
			}
		}
		return cc;
	}

}
