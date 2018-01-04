/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gearvrf.sample.controller;

import android.graphics.Color;

import java.io.IOException;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRAssetLoader;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRCursorController;
import org.gearvrf.GVREventListeners;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMaterial.GVRShaderType;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;

import org.gearvrf.GVRSphereCollider;
import org.gearvrf.GVRTexture;
import org.gearvrf.ITouchEvents;
import org.gearvrf.io.GVRInputManager;

import org.gearvrf.scene_objects.GVRCubeSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.utility.Log;

public class SampleMain extends GVRMain
{
    private static final String TAG = "SampleMain";

    private static final float SCALE = 200.0f;
    private static final float DEPTH = -7.0f;
    private static final float BOARD_OFFSET = 2.0f;

    private GVRScene mainScene;
    private GVRContext mGVRContext = null;
    private GVRActivity mActivity;
    private GVRCursorController mController;

    SampleMain(GVRActivity activity)
    {
        mActivity = activity;
    }

    @Override
    public void onInit(GVRContext gvrContext)
    {
        mGVRContext = gvrContext;
        mainScene = mGVRContext.getMainScene();

        //Create cursor
        GVRInputManager inputManager = mGVRContext.getInputManager();

        GVRTexture cursor_texture = mGVRContext.getAssetLoader().loadTexture(new GVRAndroidResource(mGVRContext, R.raw.cursor));
        final GVRSceneObject cursor = new GVRSceneObject(mGVRContext, mGVRContext.createQuad(1f, 1f), cursor_texture);
        cursor.getRenderData().setDepthTest(false);
        cursor.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.OVERLAY);

        //Initialize controller
        inputManager.selectController(new GVRInputManager.ICursorControllerSelectListener()
        {
            public void onCursorControllerSelected(GVRCursorController newController, GVRCursorController oldController)
            {
                if (oldController != null)
                {
                    oldController.removePickEventListener(mPickHandler);
                }
                mController = newController;
                newController.addPickEventListener(mPickHandler);
                newController.setCursor(cursor);
                newController.setCursorDepth(DEPTH);
                newController.setCursorControl(GVRCursorController.CursorControl.PROJECT_CURSOR_ON_SURFACE);
            }
        });

        createScene(gvrContext);
    }

    private ITouchEvents mPickHandler = new GVREventListeners.TouchEvents()
    {
        private GVRSceneObject movingObject;

        public void onEnter(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            sceneObj.getRenderData().getMaterial().setColor(Color.RED);
        }

        public void onTouchStart(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            if (movingObject == null)
            {
                sceneObj.getRenderData().getMaterial().setColor(Color.BLUE);
                if (mController.startDrag(sceneObj))
                {
                    movingObject = sceneObj;
                }
            }
        }

        public void onTouchEnd(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            sceneObj.getRenderData().getMaterial().setColor(Color.RED);
            if (sceneObj == movingObject)
            {
                mController.stopDrag();
                movingObject = null;
            }
         }

        public void onExit(GVRSceneObject sceneObj, GVRPicker.GVRPickedObject pickInfo)
        {
            sceneObj.getRenderData().getMaterial().setColor(Color.GRAY);
            if (sceneObj == movingObject)
            {
                mController.stopDrag();
                movingObject = null;
            }
        }
    };

    @Override
    public void onStep()
    {

    }


    private void createScene(GVRContext gvrContext) {
    /*
     * Adding Cubes
     */
        GVRSceneObject object = createCube();
        object.getTransform().setPosition(0.0f, BOARD_OFFSET, DEPTH);
        object.setName("MeshBoard1");
        mainScene.addSceneObject(object);

        object = createCube();
        object.getTransform().setPosition(0.0f, -BOARD_OFFSET, DEPTH);
        object.setName("MeshBoard2");
        mainScene.addSceneObject(object);

        object = createCube();
        object.getTransform().setPosition(-BOARD_OFFSET, 0.0f, DEPTH);
        object.setName("MeshBoard3");
        mainScene.addSceneObject(object);

        object = createCube();
        object.getTransform().setPosition(BOARD_OFFSET, 0.0f, DEPTH);
        object.setName("MeshBoard4");
        mainScene.addSceneObject(object);

        object = createCube();
        object.getTransform().setPosition(BOARD_OFFSET, BOARD_OFFSET, DEPTH);
        object.setName("MeshBoard5");
        mainScene.addSceneObject(object);

        object = createCube();
        object.getTransform().setPosition(BOARD_OFFSET, -BOARD_OFFSET, DEPTH);
        object.setName("MeshBoard6");
        mainScene.addSceneObject(object);

        //Add Sphere
        object = createSphere();
        object.getTransform().setPosition(-BOARD_OFFSET, BOARD_OFFSET, DEPTH);
        object.setName("SphereBoard1");
        mainScene.addSceneObject(object);

        object = createSphere();
        object.getTransform().setPosition(-BOARD_OFFSET, -BOARD_OFFSET, DEPTH);
        object.setName("SphereBoard2");
        mainScene.addSceneObject(object);

        GVRMesh mesh;
        try
        {
            mesh = mGVRContext.getAssetLoader().loadMesh(
                    new GVRAndroidResource(mGVRContext, "bunny.obj"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            mesh = null;
        }

        if (mesh == null)
        {
            mActivity.finish();
            Log.e(TAG, "Mesh was not loaded. Stopping application!");
        }

        // activity was stored in order to stop the application if the mesh is
        // not loaded. Since we don't need anymore, we set it to null to reduce
        // chance of memory leak.
        mActivity = null;

        //Create Mesh
        object = createMesh(0.75f, mesh);
        object.getTransform().setPosition(0.0f, 0.0f, DEPTH);
        object.setName("BoundsBunny1");
        attachBoundsCollider(object);
        mainScene.addSceneObject(object);

        object = createMesh(0.75f, mesh);
        object.getTransform().setPosition(4.0f, 0.0f, DEPTH);
        attachBoundsCollider(object);
        object.setName("BoundsBunny2");
        mainScene.addSceneObject(object);

        object = createMesh(0.75f, mesh);
        object.getTransform().setPosition(-4.0f, 0.0f, DEPTH);
        attachMeshCollider(object);
        object.setName("MeshBunny3");
        mainScene.addSceneObject(object);

        object = createMesh(0.75f, mesh);
        object.getTransform().setPosition(0.0f, -4.0f, DEPTH);
        attachMeshCollider(object);
        object.setName("MeshBunny4");
        mainScene.addSceneObject(object);

        //Add Skybox
        GVRAssetLoader assetLoader = gvrContext.getAssetLoader();
        GVRTexture texture = assetLoader.loadTexture(
                new GVRAndroidResource(gvrContext, R.drawable.skybox_gridroom));
        GVRMaterial material = new GVRMaterial(gvrContext);
        GVRSphereSceneObject skyBox = new GVRSphereSceneObject(gvrContext, false, material);
        skyBox.getTransform().setScale(SCALE, SCALE, SCALE);
        skyBox.getRenderData().getMaterial().setMainTexture(texture);
        mainScene.addSceneObject(skyBox);
    }

    private GVRSceneObject createCube()
    {
        GVRMaterial material = new GVRMaterial(mGVRContext, GVRShaderType.Color.ID);
        material.setColor(Color.GRAY);

        GVRCubeSceneObject cube = new GVRCubeSceneObject(mGVRContext);
        cube.getRenderData().setMaterial(material);

        attachMeshCollider(cube);

        return cube;
    }

    private GVRSceneObject createSphere()
    {
        GVRMaterial material = new GVRMaterial(mGVRContext, GVRShaderType.Color.ID);
        material.setColor(Color.GRAY);

        GVRSphereSceneObject sphere = new GVRSphereSceneObject(mGVRContext);
        sphere.getRenderData().setMaterial(material);
        sphere.getTransform().setScale(0.5f, 0.5f, 0.5f);
        //sphere.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.GEOMETRY);

        attachSphereCollider(sphere);

        return sphere;
    }

    private GVRSceneObject createMesh(float scale, GVRMesh mesh)
    {
        GVRMaterial material = new GVRMaterial(mGVRContext, GVRShaderType.Color.ID);
        material.setColor(Color.GRAY);

        GVRSceneObject meshObject = new GVRSceneObject(mGVRContext, mesh);

        meshObject.getTransform().setScale(scale, scale, scale);
        meshObject.getRenderData().setMaterial(material);
        meshObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.GEOMETRY);

        return meshObject;
    }

    private void attachMeshCollider(GVRSceneObject sceneObject)
    {
        sceneObject.attachComponent(new GVRMeshCollider(mGVRContext, false));
    }

    private void attachSphereCollider(GVRSceneObject sceneObject)
    {
        GVRSphereCollider collider = new GVRSphereCollider(mGVRContext);
        collider.setRadius(1.0f);
        sceneObject.attachComponent(collider);
    }

    private void attachBoundsCollider(GVRSceneObject sceneObject)
    {
        sceneObject.attachComponent(new GVRMeshCollider(mGVRContext, true));
    }
}
