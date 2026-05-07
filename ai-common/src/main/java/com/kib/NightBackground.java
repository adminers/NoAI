package com.genhenomiddleschool.floor.good.picturelayout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.genhenomiddleschool.floor.utils.Constants;
import com.genhenomiddleschool.floor.utils.PlayerNut;

/**
 * 深夜
 *
 * @Title: NightBackground
 * @Description: NightBackground
 * @Company: www.qiaweidata.com
 * @author: shenshilong[shilong_shen@163.com]
 * @date: 2026-05-06
 * @version: V1.0
 */
public class NightBackground extends Actor {

    private Texture nightTexture;
    private float width;
    private float height;

    public NightBackground(Texture nightTexture) {
        this.nightTexture = nightTexture;
        this.width = PlayerNut.width;
        this.height = PlayerNut.height;

        // 1. 初始化透明度为 0（白天完全看不见黑夜层）
        this.getColor().a = 0f;

        // 2. 设置进阶建议中的“冷蓝色调”：让黑夜图自带一点忧郁的蓝色感
        // 这比单纯的白色 (1,1,1) 更有电影感
        this.getColor().r = 0.8f;
        this.getColor().g = 0.8f;
        this.getColor().b = 1.0f;
    }

    /**
     * 手动触发切换的方法
     * @param isNight 是否变为黑夜
     * @param duration 渐变持续时间（秒）
     */
    public void toggleNight(boolean isNight, float duration) {
        // 清除之前没跑完的动画，防止冲突
        this.clearActions();

        if (isNight) {
            // 渐变到黑夜：透明度变为 1
            this.addAction(Actions.alpha(1.0f, duration));
        } else {
            // 渐变到白天：透明度变为 0
            this.addAction(Actions.alpha(0.0f, duration));
        }
    }

    public void toggleNight(float alpha, float duration) {
        this.clearActions();
        this.addAction(Actions.alpha(alpha, duration));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 获取当前 Actor 的颜色（由 Actions 动态改变）
        Color c = getColor();

        // 备份 Batch 原始颜色
        float oldColor = batch.getPackedColor();

        // 应用进阶建议：将 Actor 的颜色和透明度应用到 Batch
        // 这里的 parentAlpha 是为了保证如果 Actor 在 Group 里，整体透明度依然有效
        batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);

        // 绘制黑夜贴图
        batch.draw(nightTexture, 0, 0, width, height);

        // 还原 Batch 颜色（非常重要，否则会影响后面金币或其他物体的颜色）
        batch.setPackedColor(oldColor);
    }

    @Override
    public void act(float delta) {
        // 必须调用 super.act，否则 Actions 动画不会生效
        super.act(delta);
    }
}
