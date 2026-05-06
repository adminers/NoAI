package com.genhenomiddleschool.floor.good.picturelayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class CoinManagerBak2 extends Group {
    private AtlasRegion coinRegion;
    private final Pool<Coin> coinPool = Pools.get(Coin.class);

    // 定义左右两个形状
    private Polygon leftShape;
    private Polygon rightShape;

    private final float PILE_L_X = 300f;  // 左堆中心
    private final float PILE_R_X = 900f;  // 右堆中心
    private final float GROUND_Y = 1f;   // 地面基础高度

    // 统计当前两堆金币的数量，用于计算高度和宽度
    private int leftCoinCount = 0;
    private int rightCoinCount = 0;

    // --- 物理模拟参数 ---
    private final float COIN_THICKNESS = 15.0f; // 金币视觉厚度（决定堆的高度）
    private final float BASE_WIDTH = 150f;     // 山底的最大半宽度（数值越大，山越平缓）

    public CoinManagerBak2(AtlasRegion coinRegion) {
        this.coinRegion = coinRegion;
        initShapes();
    }

    private void initShapes() {
        // 左边形状坐标 (注意：Polygon 接收的是连续的 x,y 数组)
        leftShape = new Polygon(new float[]{
            0, 0,
            0, 600,
            200, 500,
            400, 300,
            600, 0
        });

        // 右边形状坐标
        rightShape = new Polygon(new float[]{
            900, 0,
            900, 200,
            1000, 600,
            1100, 700,
            1200, 600,
            1300, 200,
            1300, 0
        });
    }

    /**
     * 喷发金币
     */
    public void spawnRain(int count) {
        for (int i = 0; i < count; i++) {
            Coin coin = coinPool.obtain();
            boolean toLeft = MathUtils.randomBoolean();

            // 初始掉落位置：在天空中，中心点附近随机
            float centerX = toLeft ? PILE_L_X : PILE_R_X;
            float startX = centerX + MathUtils.random(-20, 20);
            float startY = Gdx.graphics.getHeight() + MathUtils.random(50, 500);

            coin.init(coinRegion, startX, startY, MathUtils.random(500, 800));
            coin.setUserObject(toLeft ? "LEFT" : "RIGHT");
            coin.setStacked(false);
            this.addActor(coin);
        }
    }

//    @Override
    public void act2(float delta) {
        super.act(delta);
        for (Actor actor : getChildren()) {
            if (!(actor instanceof Coin)) continue;
            Coin coin = (Coin) actor;

            if (coin.isStacked()) continue;

            boolean isLeft = "LEFT".equals(coin.getUserObject());
            int currentCount = isLeft ? leftCoinCount : rightCoinCount;
            float centerX = isLeft ? PILE_L_X : PILE_R_X;

            // --- 300个金币成山的算法 ---

            // 1. 计算当前金币所在的“层数” (Layer)
            // 假设每层比上一层少放一点金币。这里用开方函数能让山底部更宽。
            // layer 决定了高度 Y
            float layer = (float) Math.sqrt(currentCount * 12f);
            float targetY = GROUND_Y + (layer * 8f); // 8f 决定了视觉上的坡度陡峭程度

            // 落地判定
            if (coin.getY() <= targetY) {
                coin.setStacked(true);

                // 2. 计算当前层允许的最大宽度 (越往上越窄)
                // 300个金币时，底层宽度设为 120 比较合适
                float maxLayerWidth = 120f;
                float currentWidth = Math.max(10, maxLayerWidth - (layer * 12f));

                // 3. 使用正态分布（或三角形分布）让中心密集，边缘稀疏
                // MathUtils.randomTriangular 让金币更有“滚落到山脚”的感觉
                float offsetX = MathUtils.randomTriangular(-currentWidth, currentWidth, 0);

                // 4. 最终定位
                // 加入微小的 Y 偏移，防止金币完全重叠导致闪烁（Z-fighting）
                float jitterY = MathUtils.random(-2f, 2f);
                coin.setPosition(centerX + offsetX, targetY + jitterY);

                if (isLeft) leftCoinCount++;
                else rightCoinCount++;
            }
        }
    }

//    @Override
    public void act3(float delta) {
        super.act(delta);
        for (Actor actor : getChildren()) {
            if (!(actor instanceof Coin)) continue;
            Coin coin = (Coin) actor;

            if (coin.isStacked()) continue;

            boolean isLeft = "LEFT".equals(coin.getUserObject());
            int currentCount = isLeft ? leftCoinCount : rightCoinCount;
            float centerX = isLeft ? PILE_L_X : PILE_R_X;

            // --- 核心模拟算法：金币山形规则 ---
            // 1. 计算当前金币应该落到的“楼层” (每层的金币数可以根据需要调整)
            // 简单的模型：每增加一定数量的金币，高度增加一点
            float targetY = GROUND_Y + (currentCount * 0.1f * COIN_THICKNESS);

            // 落地判定
            if (coin.getY() <= targetY) {
                coin.setStacked(true);

                // 2. 计算水平偏移量（山形核心）
                // 随着金币数量增加，散落半径缩小
                // 使用三角形分布 randomTriangular 比 random 更容易让中心密集
                float currentWidth = Math.max(20, BASE_WIDTH - (currentCount * 0.05f));
                float offsetX = MathUtils.randomTriangular(-currentWidth, currentWidth, 0);

                coin.setPosition(centerX + offsetX, targetY);

                // 更新计数
                if (isLeft) leftCoinCount++;
                else rightCoinCount++;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Actor actor : getChildren()) {
            if (!(actor instanceof Coin)) continue;
            Coin coin = (Coin) actor;
            if (coin.isStacked()) continue;

            boolean isLeft = "LEFT".equals(coin.getUserObject());
            Polygon targetPolygon = isLeft ? leftShape : rightShape;

            // 落地逻辑：如果金币掉进了多边形区域，或者掉到了形状的最底端
            /*if (targetPolygon.contains(coin.getX(), coin.getY()) || coin.getY() <= 0) {
                // 找到一个形状内合法的空位并“固定”
                float[] safePos = findRandomPointInPolygon(targetPolygon);
                coin.setPosition(safePos[0], safePos[1]);
                coin.setStacked(true);

                if (isLeft) leftCoinCount++;
                else rightCoinCount++;
            }*/

            // 在 act 方法的落地判定里
            if (targetPolygon.contains(coin.getX(), coin.getY()) || coin.getY() <= 0) {
                coin.setStacked(true);

                // 传入当前的累计数量，计算该放哪
                float[] safePos = findRandomPointInPolygon(targetPolygon, isLeft ? leftCoinCount : rightCoinCount);

                coin.setPosition(safePos[0], safePos[1]);

                if (isLeft) leftCoinCount++;
                else rightCoinCount++;
            }
        }
    }

    /**
     * 在多边形内随机寻找一个点。
     * 为了让效果更像堆叠，你可以根据 currentCount 调整 y 的取值范围，
     * 比如先填满底部，再填满顶部。
     */
    private float[] findRandomPointInPolygon(Polygon poly) {
        Rectangle bounds = poly.getBoundingRectangle();
        float rx, ry;
        int maxAttempts = 20; // 防止死循环

        do {
            rx = MathUtils.random(bounds.x, bounds.x + bounds.width);
            ry = MathUtils.random(bounds.y, bounds.y + bounds.height);
            maxAttempts--;
        } while (!poly.contains(rx, ry) && maxAttempts > 0);

        return new float[]{rx, ry};
    }

    private float[] findRandomPointInPolygon(Polygon poly, int currentCount) {
        Rectangle bounds = poly.getBoundingRectangle();

        // 算法：控制随机点的 Y 轴范围，随数量增加缓慢上移
        // 这里的 300f 是你预期的总数，你可以根据实际需求调整
        float fillLevel = MathUtils.clamp(currentCount / 300f, 0, 1);

        // 计算当前层的高度区间
        // 让金币落在 [底部, 当前填满高度+一些随机余量] 之间
        float minY = bounds.y;
        float maxY = bounds.y + (bounds.height * fillLevel) + 30f;

        // 确保 maxY 不会超过多边形本身的最大高度
        maxY = Math.min(maxY, bounds.y + bounds.height);

        float rx, ry;
        int attempts = 0;
        while (true) {
            rx = MathUtils.random(bounds.x, bounds.x + bounds.width);
            ry = MathUtils.random(minY, maxY);

            // 检查点是否在定义的形状内
            if (poly.contains(rx, ry)) {
                break;
            }

            // 安全机制：如果形状太怪异导致很难随机到点，100次尝试后强行落地
            if (attempts++ > 100) {
                rx = bounds.x + bounds.width / 2;
                ry = minY + (maxY - minY) / 2;
                break;
            }
        }

        return new float[]{rx, ry};
    }

    public void removeCoins(int count, boolean fromLeft) {
        int removed = 0;
        for (int i = getChildren().size - 1; i >= 0; i--) {
            if (removed >= count) break;

            Actor actor = getChildren().get(i);
            if (actor instanceof Coin) {
                Coin coin = (Coin) actor;
                boolean isLeftSide = "LEFT".equals(coin.getUserObject());

                if (coin.isStacked() && isLeftSide == fromLeft) {
                    coin.remove();
                    coinPool.free(coin);
                    removed++;

                    if (fromLeft) leftCoinCount = Math.max(0, leftCoinCount - 1);
                    else rightCoinCount = Math.max(0, rightCoinCount - 1);
                }
            }
        }
    }
}