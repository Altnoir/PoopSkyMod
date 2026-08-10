package com.altnoir.poopsky.content.entity.model;

import com.altnoir.poopsky.content.entity.p.FlushToiletCartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FlushToiletCartModel extends EntityModel<FlushToiletCartEntity> {
    private static final float TEXTURE_WIDTH = 32.0F;
    private static final float TEXTURE_HEIGHT = 32.0F;

    private final PerFaceCube[] rootCubes = {
            cube(-4.0F, 1.0F, -7.0F, 8.0F, 3.0F, 2.0F,
                    uv(12.0F, 12.5F, 4.0F, -1.0F),
                    uv(12.0F, 10.0F, 4.0F, 1.0F),
                    uv(12.0F, 11.0F, 4.0F, 1.5F),
                    uv(12.0F, 12.5F, 4.0F, 1.5F),
                    uv(0.0F, 5.0F, 1.0F, 1.5F),
                    uv(1.0F, 5.0F, -1.0F, 1.5F)),
            cube(-4.1F, 1.9F, 2.9F, 8.2F, 10.2F, 4.2F,
                    uv(4.0F, 11.0F, 4.0F, -2.0F),
                    uv(0.0F, 9.0F, 4.0F, 2.0F),
                    uv(0.0F, 11.0F, 4.0F, 5.0F),
                    uv(6.0F, 11.0F, 4.0F, 5.0F),
                    uv(4.0F, 11.0F, 2.0F, 5.0F),
                    uv(6.0F, 11.0F, -2.0F, 5.0F)),
            cube(-4.0F, -1.0F, 0.0F, 8.0F, 5.0F, 4.0F,
                    null,
                    uv(8.0F, 9.0F, 4.0F, 2.0F),
                    uv(9.0F, 11.5F, 4.0F, 2.5F),
                    uv(5.5F, 5.0F, 4.0F, 2.5F),
                    uv(3.5F, 5.0F, 2.0F, 2.5F),
                    uv(5.5F, 5.0F, -2.0F, 2.5F)),
            cube(-4.0F, -1.0F, -6.0F, 8.0F, 2.0F, 1.0F,
                    null,
                    null,
                    uv(12.0F, 12.5F, 4.0F, 1.0F),
                    uv(12.0F, 14.0F, 4.0F, 1.0F),
                    uv(0.5F, 6.5F, 0.5F, 1.0F),
                    uv(0.5F, 6.5F, 0.5F, 1.0F)),
            cube(-4.0F, -1.0F, -5.0F, 2.0F, 5.0F, 5.0F,
                    null,
                    uv(15.0F, 7.0F, 1.0F, 2.5F),
                    null,
                    uv(8.0F, 5.0F, 1.0F, 2.5F),
                    uv(1.0F, 5.0F, 2.5F, 2.5F),
                    uv(9.5F, 5.0F, 2.5F, 2.5F)),
            cube(2.0F, -1.0F, -5.0F, 2.0F, 5.0F, 5.0F,
                    null,
                    uv(16.0F, 7.0F, -1.0F, 2.5F),
                    null,
                    uv(8.0F, 5.0F, 1.0F, 2.5F),
                    uv(12.0F, 5.0F, -2.5F, 2.5F),
                    uv(3.5F, 5.0F, -2.5F, 2.5F)),
            cube(-4.0F, -4.0F, -6.0F, 8.0F, 3.0F, 10.0F,
                    uv(4.0F, 5.0F, 4.0F, -5.0F),
                    uv(0.0F, 0.0F, 4.0F, 5.0F),
                    uv(5.0F, 7.5F, 4.0F, 1.5F),
                    uv(9.0F, 7.5F, 4.0F, 1.5F),
                    uv(0.0F, 7.5F, 5.0F, 1.5F),
                    uv(5.0F, 7.5F, -5.0F, 1.5F))
    };

    private final PerFaceCube lid = cube(-4.0F, 8.0F, 3.0F, 8.0F, 10.0F, 1.0F,
            uv(14.0F, 14.5F, -4.0F, -0.5F),
            uv(14.0F, 15.0F, -4.0F, -0.5F, 180),
            uv(16.0F, 0.0F, -4.0F, 5.0F, 180),
            uv(12.0F, 5.0F, -4.0F, -5.0F),
            uv(10.0F, 15.0F, 5.0F, 0.5F, 90),
            uv(10.0F, 15.5F, 5.0F, 0.5F, 270));

    private final PerFaceCube wheelRight = cube(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F,
            uv(20.0F, 4.0F, 1.0F, -4.0F, 180),
            uv(20.0F, 0.0F, 1.0F, 4.0F, 180),
            uv(20.0F, 0.0F, 1.0F, 4.0F, 180),
            uv(20.0F, 0.0F, 1.0F, 4.0F),
            uv(16.0F, 4.0F, 4.0F, -4.0F),
            uv(16.0F, 0.0F, 4.0F, 4.0F));

    private final PerFaceCube wheelLeft = cube(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F,
            uv(20.0F, 4.0F, 1.0F, -4.0F),
            uv(20.0F, 0.0F, 1.0F, 4.0F),
            uv(20.0F, 0.0F, 1.0F, 4.0F),
            uv(20.0F, 0.0F, 1.0F, 4.0F, 180),
            uv(16.0F, 4.0F, 4.0F, -4.0F),
            uv(16.0F, 0.0F, 4.0F, 4.0F));

    private final Vector3f[] normals = {
            new Vector3f(), new Vector3f(), new Vector3f(),
            new Vector3f(), new Vector3f(), new Vector3f()
    };

    private float wheelRightRotation;
    private float wheelLeftRotation;

    @Override
    public void setupAnim(@NotNull FlushToiletCartEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 4.0F / 16.0F, 0.0F);
        this.renderCubes(poseStack.last(), this.rootCubes, vertexConsumer, packedLight, packedOverlay, color);

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.0F / 16.0F, 11.0F / 16.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        this.renderCubes(poseStack.last(), new PerFaceCube[]{this.lid}, vertexConsumer, packedLight, packedOverlay, color);
        poseStack.popPose();
        poseStack.popPose();

        this.renderWheel(poseStack, vertexConsumer, packedLight, packedOverlay, color,
                5.0F, this.wheelRightRotation, this.wheelRight);
        this.renderWheel(poseStack, vertexConsumer, packedLight, packedOverlay, color,
                -5.0F, this.wheelLeftRotation, this.wheelLeft);
    }

    public void setupWheelRotations(FlushToiletCartEntity entity, float partialTick) {
        this.wheelLeftRotation = (float) -Math.toRadians(entity.getWheelLeftRotation(partialTick));
        this.wheelRightRotation = (float) -Math.toRadians(entity.getWheelRightRotation(partialTick));
    }

    private void renderWheel(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay,
                             int color, float x, float rotation, PerFaceCube wheel) {
        poseStack.pushPose();
        poseStack.translate(x / 16.0F, 4.0F / 16.0F, 0.0F);
        poseStack.mulPose(Axis.XP.rotation(rotation));
        this.renderCubes(poseStack.last(), new PerFaceCube[]{wheel}, consumer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    private void renderCubes(PoseStack.Pose pose, PerFaceCube[] cubes, VertexConsumer consumer,
                             int packedLight, int packedOverlay, int color) {
        Matrix3f normal = pose.normal();
        this.normals[Direction.DOWN.ordinal()].set(-normal.m10(), -normal.m11(), -normal.m12());
        this.normals[Direction.UP.ordinal()].set(normal.m10(), normal.m11(), normal.m12());
        this.normals[Direction.NORTH.ordinal()].set(-normal.m20(), -normal.m21(), -normal.m22());
        this.normals[Direction.SOUTH.ordinal()].set(normal.m20(), normal.m21(), normal.m22());
        this.normals[Direction.WEST.ordinal()].set(-normal.m00(), -normal.m01(), -normal.m02());
        this.normals[Direction.EAST.ordinal()].set(normal.m00(), normal.m01(), normal.m02());

        for (PerFaceCube cube : cubes) {
            cube.render(pose.pose(), this.normals, consumer, packedLight, packedOverlay, color);
        }
    }

    private static PerFaceCube cube(float x, float y, float z, float width, float height, float depth,
                                    FaceUv down, FaceUv up, FaceUv north, FaceUv south, FaceUv west, FaceUv east) {
        return new PerFaceCube(x, y, z, width, height, depth,
                new FaceUv[]{down, up, north, south, west, east});
    }

    private static FaceUv uv(float u, float v, float width, float height) {
        return uv(u, v, width, height, 0);
    }

    private static FaceUv uv(float u, float v, float width, float height, int rotation) {
        return new FaceUv(u, v, width, height, rotation);
    }

    private record FaceUv(float u, float v, float width, float height, int rotation) {
        private float[] vertices() {
            float u1 = this.u / TEXTURE_WIDTH;
            float v1 = this.v / TEXTURE_HEIGHT;
            float u2 = (this.u + this.width) / TEXTURE_WIDTH;
            float v2 = (this.v + this.height) / TEXTURE_HEIGHT;

            return switch (this.rotation) {
                case 90 -> new float[]{u1, v1, u1, v2, u2, v2, u2, v1};
                case 180 -> new float[]{u1, v2, u2, v2, u2, v1, u1, v1};
                case 270 -> new float[]{u2, v2, u2, v1, u1, v1, u1, v2};
                default -> new float[]{u2, v1, u1, v1, u1, v2, u2, v2};
            };
        }
    }

    private static final class PerFaceCube {
        private static final int X1_Y1_Z1 = 0;
        private static final int X2_Y1_Z1 = 1;
        private static final int X2_Y2_Z1 = 2;
        private static final int X1_Y2_Z1 = 3;
        private static final int X1_Y1_Z2 = 4;
        private static final int X2_Y1_Z2 = 5;
        private static final int X2_Y2_Z2 = 6;
        private static final int X1_Y2_Z2 = 7;

        private static final int[][] VERTEX_ORDER = {
                {X1_Y1_Z1, X2_Y1_Z1, X2_Y1_Z2, X1_Y1_Z2},
                {X1_Y2_Z2, X2_Y2_Z2, X2_Y2_Z1, X1_Y2_Z1},
                {X1_Y2_Z1, X2_Y2_Z1, X2_Y1_Z1, X1_Y1_Z1},
                {X2_Y2_Z2, X1_Y2_Z2, X1_Y1_Z2, X2_Y1_Z2},
                {X1_Y2_Z2, X1_Y2_Z1, X1_Y1_Z1, X1_Y1_Z2},
                {X2_Y2_Z1, X2_Y2_Z2, X2_Y1_Z2, X2_Y1_Z1}
        };

        private final float x;
        private final float y;
        private final float z;
        private final float width;
        private final float height;
        private final float depth;
        private final float[][] uvs = new float[Direction.values().length][];
        private final Vector3f[] vertices = {
                new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f(),
                new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()
        };
        private final Vector3f edgeX = new Vector3f();
        private final Vector3f edgeY = new Vector3f();
        private final Vector3f edgeZ = new Vector3f();

        private PerFaceCube(float x, float y, float z, float width, float height, float depth, FaceUv[] faces) {
            this.x = x / 16.0F;
            this.y = y / 16.0F;
            this.z = z / 16.0F;
            this.width = width / 16.0F;
            this.height = height / 16.0F;
            this.depth = depth / 16.0F;
            for (Direction direction : Direction.values()) {
                FaceUv face = faces[direction.ordinal()];
                if (face != null) {
                    this.uvs[direction.ordinal()] = face.vertices();
                }
            }
        }

        private void render(Matrix4f pose, Vector3f[] normals, VertexConsumer consumer,
                            int packedLight, int packedOverlay, int color) {
            this.prepareVertices(pose);
            for (Direction direction : Direction.values()) {
                int face = direction.ordinal();
                float[] faceUvs = this.uvs[face];
                if (faceUvs == null) {
                    continue;
                }

                for (int vertexIndex = 0; vertexIndex < 4; vertexIndex++) {
                    Vector3f vertex = this.vertices[VERTEX_ORDER[face][vertexIndex]];
                    Vector3f normal = normals[face];
                    consumer.addVertex(vertex.x(), vertex.y(), vertex.z())
                            .setColor(color)
                            .setUv(faceUvs[vertexIndex * 2], faceUvs[vertexIndex * 2 + 1])
                            .setOverlay(packedOverlay)
                            .setLight(packedLight)
                            .setNormal(normal.x(), normal.y(), normal.z());
                }
            }
        }

        private void prepareVertices(Matrix4f pose) {
            this.edgeX.set(pose.m00(), pose.m01(), pose.m02()).mul(this.width);
            this.edgeY.set(pose.m10(), pose.m11(), pose.m12()).mul(this.height);
            this.edgeZ.set(pose.m20(), pose.m21(), pose.m22()).mul(this.depth);

            this.vertices[X1_Y1_Z1].set(this.x, this.y, this.z).mulPosition(pose);
            this.vertices[X1_Y1_Z1].add(this.edgeX, this.vertices[X2_Y1_Z1]);
            this.vertices[X2_Y1_Z1].add(this.edgeY, this.vertices[X2_Y2_Z1]);
            this.vertices[X1_Y1_Z1].add(this.edgeY, this.vertices[X1_Y2_Z1]);
            this.vertices[X1_Y1_Z1].add(this.edgeZ, this.vertices[X1_Y1_Z2]);
            this.vertices[X2_Y1_Z1].add(this.edgeZ, this.vertices[X2_Y1_Z2]);
            this.vertices[X2_Y2_Z1].add(this.edgeZ, this.vertices[X2_Y2_Z2]);
            this.vertices[X1_Y2_Z1].add(this.edgeZ, this.vertices[X1_Y2_Z2]);
        }
    }
}
