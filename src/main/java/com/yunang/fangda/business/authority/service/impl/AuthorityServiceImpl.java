package com.yunang.fangda.business.authority.service.impl;

import com.yunang.fangda.business.authority.jpa.AuthorityJpa;
import com.yunang.fangda.business.authority.model.AuthorityModel;
import com.yunang.fangda.business.authority.service.AuthorityService;
import com.yunang.fangda.business.jurisdiction.jpa.JurisdictionJpa;
import com.yunang.fangda.business.jurisdiction.model.JurisdictionModel;
import com.yunang.fangda.business.position.jpa.PositionJpa;
import com.yunang.fangda.business.position.model.PositionModel;
import com.yunang.fangda.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: LD
 * @date:
 * @description:
 */
@Slf4j
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityJpa jpa;
    @Autowired
    private JurisdictionJpa jurisdictionJpa;
    @Autowired
    private PositionJpa positionJpa;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseResult<AuthorityModel> setPostIdAndJurId(AuthorityModel model) {
        AuthorityModel one = jpa.findByPositionModelUuidAndJurisdictionModelUuid(model.getPositionModel().getUuid(), model.getJurisdictionModel().getUuid());
        if (one != null) {
            jpa.deleteById(one.getUuid());
        } else {
            if (model.getJurisdictionModel() != null && model.getJurisdictionModel().getUuid() != null) {
                Optional<JurisdictionModel> optional = jurisdictionJpa.findById(model.getJurisdictionModel().getUuid());
                if (optional.isPresent()) {
                    model.setJurisdictionModel(optional.get());
                }
            }
            if (model.getPositionModel() != null && model.getPositionModel().getUuid() != null) {
                Optional<PositionModel> optional = positionJpa.findById(model.getPositionModel().getUuid());
                if (optional.isPresent()) {
                    model.setPositionModel(optional.get());
                }
            }
            jpa.save(model);
        }
        return new ResponseResult<>(true, "成功");
    }

    @Override
    public ResponseResult<List<JurisdictionModel>> findByAutPosId(String autPosId) {
        List<Object[]> list = jpa.findJurisdictionModelByAutPostId(autPosId);
        if (list.size() > 0) {
            List<JurisdictionModel> list1 = new ArrayList<>();
            list.forEach(k -> {
                if (k[0] != null) {
                    JurisdictionModel model = new JurisdictionModel((String) k[0], (String) k[1], (String) k[2], (String) k[3], (Integer) k[4], new ArrayList<>());
                    list1.add(model);
                }
            });
            List<JurisdictionModel> dg = dg(list1);
            return new ResponseResult<>(true, "成功", dg);
        } else {
            return new ResponseResult<>(false, "未查询到记录");
        }
    }

    @Override
    public ResponseResult<List<JurisdictionModel>> findByAutPosId2(String autPosId) {
        List<Object[]> list = jpa.findJurisdictionModelByAutPostId(autPosId);
        if (list.size() > 0) {
            List<JurisdictionModel> list1 = new ArrayList<>();
            list.forEach(k -> {
                if (k[0] != null) {
                    JurisdictionModel model = new JurisdictionModel((String) k[0], (String) k[1], (String) k[2], (String) k[3], (Integer) k[4], new ArrayList<>());
                    list1.add(model);
                }
            });
            return new ResponseResult<>(true, "成功", list1);
        } else {
            return new ResponseResult<>(false, "未查询到记录");
        }
    }

    private static List<JurisdictionModel> dg(List<JurisdictionModel> list) {
        List<JurisdictionModel> all = new ArrayList<>();
        list.forEach(k -> {
            if (k.getJurParent().equals("0")) {
                all.add(findChildren(k, list));
            }
        });
        return all;
    }

    private static JurisdictionModel findChildren(JurisdictionModel treeNode, List<JurisdictionModel> treeNodes) {
        for (JurisdictionModel it : treeNodes) {
            if (treeNode.getUuid().equals(it.getJurParent())) {
                treeNode.getList().add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }
}
