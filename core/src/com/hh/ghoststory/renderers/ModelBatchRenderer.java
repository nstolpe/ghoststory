package com.hh.ghoststory.renderers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;

/**
 * Created by nils on 1/24/15.
 */
public class ModelBatchRenderer extends AbstractRenderer {
    private Environment environment = new Environment();
    private ModelBatch modelBatch = new ModelBatch();
    private PerspectiveCamera pCamera;
    private OrthographicCamera oCamera;
    private Camera activeCamera;

    @Override
    public void render() {

    }

    public void setUpLights(BaseLight[] lights) {
        environment.add(lights);
    }

    public void setUpPerspectiveCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
        this.pCamera = new PerspectiveCamera(fieldOfViewY, viewportWidth, viewportHeight);
        this.pCamera.position.set(10, 10, 10);
        this.pCamera.direction.set(-1, -1, -1);
        this.pCamera.near = 1;
    }

    public void setUpIsomorphicCamera() {

    }
}
