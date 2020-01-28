package com.jamesdpeters.helpers;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Coord3ds;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import java.util.List;

public class MultiScatter extends AbstractDrawable implements ISingleColorable {

    public MultiScatter(List<List<Coord3d>> coordinates, Color[] rgb, float width) {
        bbox = new BoundingBox3d();
        setData(coordinates);
        setWidth(width);
        setColors(rgb);
    }


    public void clear() {
        coordinates = null;
        bbox.reset();
    }

    /* */

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        if (gl.isGL2()) {
            drawGL2(gl);
        } else {
            drawGLES2();
        }

        doDrawBounds(gl, glu, cam);
    }

    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);

        GLES2CompatUtils.glBegin(GL.GL_POINTS);
        if (colors == null)
            GLES2CompatUtils.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
        if (coordinates != null) {
            int k = 0;
            for (List<Coord3d> cArray : coordinates) {
                for(Coord3d c : cArray) {
                    if (colors != null) {
                        GLES2CompatUtils.glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
                    }
                    GLES2CompatUtils.glVertex3f(c.x, c.y, c.z);
                }
                k++;
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);

        gl.getGL2().glBegin(GL.GL_POINTS);
        if (colors == null)
            gl.getGL2().glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
        if (coordinates != null) {
            int k = 0;
            for (List<Coord3d> cArray : coordinates) {
                for(Coord3d c : cArray) {
                    if (colors != null) {
                        gl.getGL2().glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
                    }
                    gl.getGL2().glVertex3f(c.x, c.y, c.z);
                }
                k++;
            }
        }
        gl.getGL2().glEnd();
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        for (List<Coord3d> cArray : coordinates) {
            for (Coord3d c : cArray) {
                c.set(transform.compute(c));
            }
        }
        updateBounds();
    }

    /* */

    /**
     * Set the coordinates of the point.
     *
     * @param coordinates
     *            point's coordinates
     */
    public void setData(List<List<Coord3d>> coordinates) {
        this.coordinates = coordinates;

        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        System.out.println("Updating bounds!");
        for (List<Coord3d> cArray : coordinates) {
            for (Coord3d c : cArray) {
                bbox.add(c);
            }
        }
        bbox.setXmax(Math.max(bbox.getXmax(),bbox.getYmax()));
        bbox.setXmin(Math.min(bbox.getXmin(),bbox.getYmin()));

        bbox.setYmax(Math.max(bbox.getXmax(),bbox.getYmax()));
        bbox.setYmin(Math.min(bbox.getXmin(),bbox.getYmin()));

        bbox.setZmax(bbox.getXmax());
        bbox.setZmin(bbox.getXmin());
    }

    public List<List<Coord3d>> getData() {
        return coordinates;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public void setColor(Color color) {
        this.rgb = color;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public Color getColor() {
        return rgb;
    }

    /**
     * Set the width of the point.
     *
     * @param width
     *            point's width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**********************************************************************/

    public Color[] colors;
    public List<List<Coord3d>> coordinates;
    public Color rgb;
    public float width;
}
