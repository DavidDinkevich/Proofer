package geometry.shapes;

import geometry.Vec2;

/**
 * {@link Scalable} objects can be scaled.
 * @author David Dinkevich
 *
 */
interface Scalable {
	public Vec2 getScale();
	public Vec2 getDilationPoint();
	public void setScale(Vec2 scale);
	public void setScale(Vec2 scale, Vec2 dilationPoint);
}
