package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.lights.PointLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;

public class IsometricScreen extends AbstractScreen implements InputProcessor {
	final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
	final Vector3 intersection = new Vector3();
	final Vector3 curr = new Vector3();
	final Vector3 last = new Vector3(-1, -1, -1);
	final Vector3 delta = new Vector3();
	
	protected String vertexShader;
	protected String fragmentShader;
	
	private boolean justDragged = false;
	private Ghost ghost;
	
	private ModelBatch modelBatch;
//	private ModelLoader loader;
	public boolean loading;
	public AssetManager assets;
	public Array<GameModel> game_models = new Array<GameModel>();
	public Lights lights;
	
	public IsometricScreen(GhostStory game) {
		super(game);
		
		modelBatch = new ModelBatch();
		camera = new OrthographicCamera(10, 10 * (Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth()));
		// These set the isometric perspective.
		camera.position.set(5, 5, 5);
		camera.direction.set(-1, -1, -1);
		camera.near = 1;
		camera.far = 100;
		
        ghost = new Ghost();
        game_models.add(ghost);
        
        for(int z = 0; z < 10; z++) {
			for(int x = 0; x < 10; x++) {
				game_models.add(new Tile(x,0,z));
			}
		}
        
        lights = new Lights();
        lights.add(new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 2, 0, 1f));
        lights.add(new PointLight().set(new Color(1f,0f,0f, 1f), 4,2,4, 1));
        lights.add(new PointLight().set(new Color(0f,0f,1f, 1f), 6,2,0f, 1));
        lights.ambientLight.set(0.2f, 0.2f, 0.2f, 1f);
        lights.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -1f, -1f));
        assets = new AssetManager();

        for (GameModel game_model : game_models) {
        	if (!assets.isLoaded(game_model.model_resource)) assets.load(game_model.model_resource, Model.class);
        }
		loading = true;
		
		setClear(0.5f, 0.5f, 0.5f, 1f);
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
//        if (loading && assets.update())
//        	doneLoading();
        
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
// Keep these, might need them when other stuff comes back in.
//	    Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
//	    Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
//	    Gdx.gl20.glClearDepthf(1.0f);
//	    Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
//	    Gdx.gl20.glDepthFunc(GL20.GL_LESS);
		camera.update();
	    
		if (doneLoading()) {
			modelBatch.begin(camera);
	
			for (GameModel game_model : game_models) {
				game_model.update();
				modelBatch.render(game_model.model, lights);
			}
			modelBatch.end();
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		justDragged = false;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (justDragged == false) {
			Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

			ghost.setStartPosition(ghost.position);
			ghost.setTargetPosition(intersection.x, 0, intersection.z);
			

			float rotation = MathUtils.atan2(intersection.x - ghost.position.x, intersection.z - ghost.position.z) * 180 / MathUtils.PI;
			ghost.setTargetRotation(rotation < 0 ? rotation += 360 : rotation);
		} else {
			justDragged = false;
		}
		last.set(-1, -1, -1);
		return false;
	}

	/*
	 * Lets the screen be dragged and moved. Taken from libgdx isometric tutorial.
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Ray pickRay = camera.getPickRay(screenX, screenY);
		Intersector.intersectRayPlane(pickRay, xzPlane, curr);
 
		if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
			pickRay = camera.getPickRay(last.x, last.y);
			Intersector.intersectRayPlane(pickRay, xzPlane, delta);	
			delta.sub(curr);
			camera.position.add(delta.x, delta.y, delta.z);
		}
		last.set(screenX, screenY, 0);
		System.out.println("drag");
		justDragged = true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
    private boolean doneLoading() {
    	if (loading && assets.update() != true) {
    		return false;
    	} else if (loading && assets.update()){
        	for (GameModel game_model : game_models) {
        		game_model.setModelResource(assets);
        	}
        	loading = false;
        	return false;
    	}
    	return true;
    }
	
    /*
     * Basic game model class.
     */
    abstract class GameModel {
    	public String model_resource;
    	public String texture_resource;
    	public ModelInstance model;
    	public Vector3 position = new Vector3(0,0,0);
    	public float rotation = 0;
    	public Vector3 verticalAxis = new Vector3(0,1,0);;
    	
    	public void update() {
    		setRotation();
			setTranslation();
			model.transform.setToTranslation(position);
			model.transform.rotate(verticalAxis, rotation);
    	}
    	
    	abstract public void setRotation();
    	abstract public void setTranslation();
    	
    	// Retrieves a model resource from an asset manager and attaches it to the game model.
    	public void setModelResource(AssetManager assets) {
    		model = new ModelInstance(assets.get(model_resource, Model.class));
    	}
    }
    
    class StaticModel extends GameModel {
		@Override
		public void setRotation() {
			// TODO Auto-generated method stub
		}

		@Override
		public void setTranslation() {
			// TODO Auto-generated method stub
		}
    }
    
    class DynamicModel extends GameModel {
		@Override
		public void setRotation() {
			// TODO Auto-generated method stub
		}

		@Override
		public void setTranslation() {
			// TODO Auto-generated method stub
		}
    }
    
	/*
	 * Tiles for making the ground. Shouldn't stay here forever.
	 */
	class Tile extends StaticModel {
		public Tile(Vector3 position) {
			model_resource = "models/tile.g3dj";
			position.set(position);
		}
		
		public Tile(int x, int y, int z) {
			model_resource = "models/tile.g3dj";
			position.set(x,y,z);
			verticalAxis = new Vector3(0,1,0);
		}
		
		@Override
		public void update() {
			setRotation();
			setTranslation();
			model.transform.setToTranslation(position);
			model.transform.rotate(verticalAxis, rotation);
		}
	}
	
	/*
	 * The ghost. Also shouldn't stay here forever.
	 */
	class Ghost extends DynamicModel {
		private float targetRotation = 45;
		public float rotation = 45;
		private Vector3 targetPosition = new Vector3();
		private Vector3 startPosition = new Vector3();
		// y direction vector to rotate around.
		private int rotationDirection;
		private static final int NONE = 0;
		private static final int CLOCKWISE = 1;
		private static final int COUNTER_CLOCKWISE = 2;
		private float speed = 2;
		
		public Ghost() {
			model_resource = "models/ghost.g3dj";
	        setPosition(new Vector3(0, 0, 0));
	        rotationDirection = NONE;
	        startPosition.set(position);
		}
		
		/*
		 * Sets position and target position. Could be used for teleport.
		 */
		public void setPosition(Vector3 position) {
			this.position.set(position);
			this.setTargetPosition(position);
		}
		
		/*
		 * Returns a copy so nothing happens to the position vector.
		 */
		public Vector3 getPosition() {
			return position.cpy();
		}
		
		public void update() {
			setRotation();
			setTranslation();
			model.transform.setToTranslation(position);
			model.transform.rotate(verticalAxis, rotation);
		}
		@Override
		public void setTranslation() {
			// Only move if a targetPosition hasn't been reached or exceeded and it's not rotating.
			if (!position.epsilonEquals(targetPosition, 0f) && rotationDirection == NONE) {
				if (position.dst(startPosition) >= targetPosition.dst(startPosition)) {
					position.set(targetPosition);
				} else {
					position.lerp(targetPosition, Gdx.graphics.getDeltaTime()*(speed/targetPosition.dst(position)));
				}
			}
		}
		@Override
		public void setRotation() {
			if (rotation != targetRotation) {	
				if (rotationDirection == NONE) {
					setRotationDirection();
				} else if (rotationDirection == CLOCKWISE) {
					rotateClockwise();
				} else if (rotationDirection == COUNTER_CLOCKWISE) {
					rotateCounterClockwise();
				}
			}
		}
		
		private void rotateClockwise() {
			rotation += Gdx.graphics.getDeltaTime() * 100.0f;
			if (rotation >= targetRotation) {
				rotation = targetRotation > 360 ? targetRotation - 360 : targetRotation;
				targetRotation = rotation;
				rotationDirection = NONE;
			}
		}
		
		private void rotateCounterClockwise() {
			rotation -= Gdx.graphics.getDeltaTime() * 100.0f;
			if (rotation <= targetRotation) {
				rotation = targetRotation < 0 ? targetRotation + 360 : targetRotation;
				targetRotation = rotation;
				rotationDirection = NONE;
			}
		}
		
		private void setRotationDirection() {
			if (targetRotation > rotation && targetRotation - rotation < 180) {
				rotationDirection = CLOCKWISE;
			}
			if (targetRotation < rotation && rotation - targetRotation < 180) {
				rotationDirection = COUNTER_CLOCKWISE;
			}
			if (targetRotation > rotation && targetRotation - rotation >= 180) {
				rotation += 360;
				rotationDirection = COUNTER_CLOCKWISE;
			}
			if (targetRotation < rotation && rotation - targetRotation >= 180) {
				targetRotation += 360;
				rotationDirection = CLOCKWISE;
			}
		}
		
		public void setTargetPosition(Vector3 point) {
			targetPosition.set(point);
		}

		public void setTargetPosition(float x, float y, float z) {
			targetPosition.set(x, y, z);
		}
		
		public void setStartPosition(Vector3 point) {
			startPosition.set(point);
		}
		
		public void setStartPosition(float x, float y, float z) {
			startPosition.set(x, y, z);
		}
		
		public void setTargetRotation(float angle) {
			resetRotation();
			this.targetRotation = angle;
		}
		
		private void resetRotation() {
			if (rotation > 360) rotation -= 360;
			rotationDirection = NONE;
		}
	}
	

}