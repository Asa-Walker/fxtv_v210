package com.fxtv.threebears.model;

import java.io.Serializable;
import java.util.List;

/**
 * 用户实体类
 *
 * @author 薛建浩
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public String user_id;
    public String username;
    /**
     * 未加密的用户id
     */
    public String uid;
    /**
     * 用户昵称
     */
    public String nickname;
    /**
     * 用户头像
     */
    public String image;
    /**
     * 用户经验值
     */
    public String exp;
    /**
     * 用户等级
     */
    public String level;
    /**
     * 用户的饼干
     */
    public String currency;
    /**
     * 距离下一级的经验值
     */
    public String upgrade_exp;
    /**
     * 用户是否签到 1--已签到 0--未签到
     */
    public String sign_status;
    /**
     * 手机号码
     */
    public String phone;
    /**
     * 增长的经验和饼干
     */
    public String show_tip;
    /**
     * 是否是显示新手引导 1--显示新手引导 0--不显示新手引导
     */
    public String guide_status;
    /**
     * QQ号码
     */
    public String qq;
    /**
     * 性别
     */
    public String sex;
    /**
     * 所在地
     */
    public String address;
    /**
     * 生日
     */
    public String birthday;
    /**
     * 邀请码
     */
    public String recommend_code;
    /**
     * 感兴趣的游戏
     */
    public String intro;

    /**
     * 熊掌数
     */
    public long paw;

    public String guard_anchor;

    /**
     * 订阅的主播列表
     */
    public List<Anchor> order_anchor;

    /**
     * 是否可以修改昵称(0:没有修改过昵称；1：已经修改过昵称)
     */
    public String modified_nickname;
    public String reward_tips;//注册奖励提示图片url（未奖励url为空）


    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", image='" + image + '\'' +
                ", exp='" + exp + '\'' +
                ", level='" + level + '\'' +
                ", currency='" + currency + '\'' +
                ", upgrade_exp='" + upgrade_exp + '\'' +
                ", sign_status='" + sign_status + '\'' +
                ", phone='" + phone + '\'' +
                ", show_tip='" + show_tip + '\'' +
                ", guide_status='" + guide_status + '\'' +
                ", qq='" + qq + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                ", recommend_code='" + recommend_code + '\'' +
                ", intro='" + intro + '\'' +
                ", paw=" + paw +
                ", guard_anchor='" + guard_anchor + '\'' +
                ", order_anchor=" + order_anchor +
                ", modified_nickname='" + modified_nickname + '\'' +
                ", reward_tips='" + reward_tips + '\'' +
                '}';
    }
}
