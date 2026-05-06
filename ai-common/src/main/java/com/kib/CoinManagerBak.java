package com.genhenomiddleschool.floor.good.picturelayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class CoinManagerBak extends Group {
    private AtlasRegion coinRegion;
    private final Pool<Coin> coinPool = Pools.get(Coin.class);

    private final float PILE_L_X = 100f; // 左堆中心
    private final float PILE_R_X = 1100f; // 右堆中心

    private float leftHeight = 0;
    private float rightHeight = 0;

    // 每次金币堆叠时增加的高度增量（数值越小，堆得越密）
    private final float HEIGHT_STEP = 0.8f;

    public CoinManagerBak(AtlasRegion coinRegion) {
        this.coinRegion = coinRegion;
    }

    /*
    * 细节优化点：
    视觉连贯性：在 removeCoins 时，金币是直接消失的。如果你想做得更细致，可以给金币加一个 Action 动画，比如让它向上飘动并逐渐透明后再回收：

    Java
    // 在 removeCoins 里的 coin.remove() 之前改用这个：
    coin.addAction(Actions.sequence(
        Actions.parallel(Actions.moveBy(0, 100, 0.3f), Actions.fadeOut(0.3f)),
        Actions.removeActor(),
        Actions.run(() -> coinPool.free(coin))
    ));
    * */

    /**
     * 减少金币的方法
     * @param count 减少的数量
     * @param fromLeft 是否从左边堆拿走
     */
    public void removeCoins(int count, boolean fromLeft) {
        int removed = 0;
        // 从后往前遍历（Children 列表里最后的 Actor 是最晚加入、在最上层的）
        for (int i = getChildren().size - 1; i >= 0; i--) {
            if (removed >= count) break;

            Actor actor = getChildren().get(i);
            if (actor instanceof Coin) {
                Coin coin = (Coin) actor;
                String side = (String) coin.getUserObject();
                boolean isLeftSide = "LEFT".equals(side);

                // 只有处于堆叠状态且位置正确的金币才会被拿走
                if (coin.isStacked()/* && isLeftSide == fromLeft*/) {
                    coin.remove(); // 从舞台移除
                    coinPool.free(coin); // 回收到对象池
                    removed++;

                    // 降低对应山的高度
                    if (fromLeft) {
                        leftHeight = Math.max(0, leftHeight - HEIGHT_STEP);
                    } else {
                        rightHeight = Math.max(0, rightHeight - HEIGHT_STEP);
                    }
                }
            }
        }
    }

    /**
     * 喷发金币（增加）
     */
    public void spawnRain(int count) {
        for (int i = 0; i < count; i++) {
            Coin coin = coinPool.obtain();
            boolean toLeft = MathUtils.randomBoolean();
            float targetX = toLeft ? PILE_L_X : PILE_R_X;

            // 增加随机散落感
            float startX = targetX + MathUtils.randomTriangular(-70, 70, 0);
            float startY = Gdx.graphics.getHeight() + MathUtils.random(50, 500);

            coin.init(coinRegion, startX, startY, MathUtils.random(400, 700));
            coin.setUserObject(toLeft ? "LEFT" : "RIGHT");
            this.addActor(coin);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Actor actor : getChildren()) {
            Coin coin = (Coin) actor;
            if (coin.isStacked()) continue;

            float ground = "LEFT".equals(coin.getUserObject()) ? leftHeight : rightHeight;

            // 落地判定（基础高度 10 是为了不要紧贴屏幕底边）
            if (coin.getY() <= ground + 10) {
                coin.setStacked(true);
                coin.setY(ground + 10 + MathUtils.random(-3, 3));

                if ("LEFT".equals(coin.getUserObject())) leftHeight += HEIGHT_STEP;
                else rightHeight += HEIGHT_STEP;
            }
        }
    }

}