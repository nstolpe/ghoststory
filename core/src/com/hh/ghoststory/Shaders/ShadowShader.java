package com.hh.ghoststory.Shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShadowShader extends DefaultShader {
	public Renderable renderable;
    public static class Config extends DefaultShader.Config {
        public boolean depthBufferOnly = false;
        public float defaultAlphaTest = 0.5f;

        public Config () {
            super();
// wasn't rendering lights w/ this on. specific to depth?
//            defaultCullFace = GL20.GL_FRONT;
        }

        public Config (String vertexShader, String fragmentShader) {
            super(vertexShader, fragmentShader);
        }
    }

    private static String defaultVertexShader = null;

    public final static String getDefaultVertexShader () {
        if (defaultVertexShader == null)
            defaultVertexShader = Gdx.files.internal("shaders/new.vertex.glsl").readString();
        return defaultVertexShader;
    }

    private static String defaultFragmentShader = null;

    public final static String getDefaultFragmentShader () {
        if (defaultFragmentShader == null)
            defaultFragmentShader = Gdx.files.internal("shaders/new.fragment.glsl").readString();
        return defaultFragmentShader;
    }

//    public static String createPrefix (final Renderable renderable, final Config config) {
//        String prefix = DefaultShader.createPrefix(renderable, config);
//        if (!config.depthBufferOnly) prefix += "#define PackedDepthFlag\n";
//        return prefix;
//    }

//    public final int numBones;
//    public final int weights;
//    private final FloatAttribute alphaTestAttribute;

    public ShadowShader (final Renderable renderable, final DefaultShader.Config config) {
        super(renderable, config, createPrefix(renderable, config));
    }

    /**
     * Adds the shadowFlag normal to the shader prefix from `DefaultShader.createPrefix()`
     * @param renderable
     * @param config
     * @return
     */
    public static String createPrefix(final Renderable renderable, final DefaultShader.Config config) {
        String prefix = DefaultShader.createPrefix(renderable, config);
        prefix += "#define shadowFlag\n";
        // Append extra stuff here, like shadowFlag
        return prefix;
    }
    public ShadowShader (final Renderable renderable) {
        this(renderable, new Config());
    }

    public ShadowShader (final Renderable renderable, final Config config) {
        this(renderable, config, createPrefix(renderable, config));
    }

    public ShadowShader (final Renderable renderable, final Config config, final String prefix) {
        this(renderable, config, prefix, config.vertexShader != null ? config.vertexShader : getDefaultVertexShader(),
                config.fragmentShader != null ? config.fragmentShader : getDefaultFragmentShader());
    }

    public ShadowShader (final Renderable renderable, final Config config, final String prefix, final String vertexShader,
                        final String fragmentShader) {
        this(renderable, config, new ShaderProgram(prefix + vertexShader, prefix + fragmentShader));
    }

    public ShadowShader (final Renderable renderable, final Config config, final ShaderProgram shaderProgram) {
        super(renderable, config, shaderProgram);
        final Attributes attributes = combineAttributes(renderable);
//        this.numBones = renderable.bones == null ? 0 : config.numBones;
//        int w = 0;
//        final int n = renderable.mesh.getVertexAttributes().size();
//        for (int i = 0; i < n; i++) {
//            final VertexAttribute attr = renderable.mesh.getVertexAttributes().get(i);
//            if (attr.usage == Usage.BoneWeight) w |= (1 << attr.unit);
//        }
	    this.renderable = renderable;
        this.program = shaderProgram;
//        weights = w;
//        alphaTestAttribute = new FloatAttribute(FloatAttribute.AlphaTest, config.defaultAlphaTest);
    }

    @Override
    public void begin (Camera camera, RenderContext context) {
        super.begin(camera, context);
        // Gdx.gl20.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
        // Gdx.gl20.glPolygonOffset(2.f, 100.f);
	    // maybe use below
//        context.setDepthTest(GL20.GL_LEQUAL);
//        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void end () {
        super.end();
         Gdx.gl20.glDisable(GL20.GL_POLYGON_OFFSET_FILL);
    }

    @Override
    public boolean canRender (Renderable renderable) {
        final Attributes attributes = combineAttributes(renderable);
        if (attributes.has(BlendingAttribute.Type)) {
            if ((attributesMask & BlendingAttribute.Type) != BlendingAttribute.Type)
                return false;
            if (attributes.has(TextureAttribute.Diffuse) != ((attributesMask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse))
                return false;
        }
        final boolean skinned = ((renderable.mesh.getVertexAttributes().getMask() & Usage.BoneWeight) == Usage.BoneWeight);
//        if (skinned != (numBones > 0)) return false;
        if (!skinned) return true;
        int w = 0;
        final int n = renderable.mesh.getVertexAttributes().size();
        for (int i = 0; i < n; i++) {
            final VertexAttribute attr = renderable.mesh.getVertexAttributes().get(i);
            if (attr.usage == Usage.BoneWeight) w |= (1 << attr.unit);
        }
//        return w == weights;
    return true;
    }

    @Override
    public void render (Renderable renderable, Attributes combinedAttributes) {
		super.render(renderable, combinedAttributes);
    }

    private final static Attributes tmpAttributes = new Attributes();
    // TODO: Move responsibility for combining attributes to RenderableProvider
    private static final Attributes combineAttributes(final Renderable renderable) {
        tmpAttributes.clear();
        if (renderable.environment != null) tmpAttributes.set(renderable.environment);
        if (renderable.material != null) tmpAttributes.set(renderable.material);
        return tmpAttributes;
    }
}

