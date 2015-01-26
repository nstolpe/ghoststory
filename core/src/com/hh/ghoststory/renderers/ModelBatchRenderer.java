package com.hh.ghoststory.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.utils.Array;

/**
 * Created by nils on 1/24/15.
 */
public class ModelBatchRenderer extends AbstractRenderer {
    public static final int PERSP = 0;
    public static final int ORTHO= 1;

    private Environment environment = new Environment();
    private ModelBatch modelBatch;
    private PerspectiveCamera pCamera;
    private OrthographicCamera oCamera;
    private int activeCamera;
    private Array<ModelInstance> modelInstances;
    public AssetManager assetManager = new AssetManager();

    public ModelBatchRenderer() {
        setModelBatch(new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl")));
    }

    public void setUpDefaultCamera(int type) {
        switch (type) {
            case PERSP:
                setUpPerspectiveCamera();
                setActiveCamera(PERSP);
                break;
            case ORTHO:
                setUpOrthograhcicCamera();
                setActiveCamera(ORTHO);
                break;
            default:
                break;
        }
    }
//    public void setUpDefaultCamera(Camera camera) {
//        if (camera instanceof PerspectiveCamera) {
//
//        }
//        switch (type) {
//            case PERSP:
//                setUpPerspectiveCamera();
//                setActiveCamera(PERSP);
//                break;
//            case ORTHO:
//                setUpOrthograhcicCamera();
//                setActiveCamera(ORTHO);
//                break;
//            default:
//                break;
//        }
//    }

    public void setModelBatch(ModelBatch modelBatch) {
        this.modelBatch = modelBatch;
    }

    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    @Override
    public void render() {
        Camera camera = getActiveCamera();
        camera.update();
        modelBatch.begin(camera);
        for (ModelInstance model : modelInstances)
            modelBatch.render(model, environment);
        modelBatch.end();
    }

    @Override
    public void setRenderables(Array modelInstances) {
        this.modelInstances = modelInstances;
    }

    public void setUpLights(BaseLight[] lights) {
        environment.add(lights);
    }

    /*
     * Sets up the perspective camera for entire screen/window.
     * @TODO parameterize more of this.
     */
        public void setUpPerspectiveCamera() {
        setUpPerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    /*
     * Sets up the perspective camera.
     * @TODO parameterize more of this.
     */
    public void setUpPerspectiveCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
        pCamera = new PerspectiveCamera(fieldOfViewY, viewportWidth, viewportHeight);
        pCamera.position.set(10, 10, 10);
        pCamera.direction.set(-1, -1, -1);
        pCamera.near = 1;
    }
    /*
     * @TODO: Make not suck
     */
    public void setUpOrthograhcicCamera() {
        setUpOrthograhcicCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    /*
     * @TODO: Make not suck
     */
    public void setUpOrthograhcicCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
        oCamera = new OrthographicCamera();
        oCamera.setToOrtho(false, 20, 20 * (viewportWidth / viewportHeight));
//        oCamera.position.set(100, 100, 100);
        oCamera.position.set(10, 10, 10);
        oCamera.direction.set(-1, -1, -1);
        oCamera.near = 1;
        oCamera.far = 300;
    }

    /*
     * Sets the activeCamera field. This camera will be used for all rendering.
     */
    public void setActiveCamera(int type) {
        this.activeCamera = type;
    }
    /*
     * Returns the active camera.
     */
    public Camera getActiveCamera() {
        Camera camera = null;
        switch (activeCamera) {
            case PERSP:
                camera = getPerspectiveCamera();
                break;
            case ORTHO:
                camera = getOrthograhcicCamera();
                break;
            default:
                break;
        }
        return camera;
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return pCamera;
    }

    public OrthographicCamera getOrthograhcicCamera() {
        return oCamera;
    }
    @Override
    public void dispose() {
        modelBatch.dispose();
    }
}
