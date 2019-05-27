package geometry.shapes;

import geometry.Dimension;
import geometry.Vec2;
import geometry.proofs.ProofUtils;
import javafx.scene.shape.ArcType;


public class Arc extends Vertex {
	
	private Dimension.Mutable size;
	private float startAngle;
	private float stopAngle;
	private ArcType arcType;
			
	public Arc(Vec2 loc, Dimension size, float startAngle, float stopAngle, 
			ArcType arcType) {
		super(loc);
		this.size = new Dimension.Mutable(size);
		this.startAngle = startAngle;
		this.stopAngle = stopAngle;
		this.arcType = arcType;
	}
	
	public Arc(Vec2 loc, Dimension size, float startAngle, float endAngle) {
		this(loc, size, startAngle, endAngle, ArcType.ROUND);
	}

	public Arc(char name) {
		this(Vec2.ZERO, Dimension.ONE, 0f, 0f);
	}

	public Arc() {
		this('\0');
	}
	
	public Arc(Arc other) {
		super(other);
		size.set(other.size);
		startAngle = other.startAngle;
		stopAngle = other.stopAngle;
		arcType = other.arcType;
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size.set(size);
	}

	/**
	 * Get whether the given string is a valid name for an {@link Arc}.
	 * @param name the name to be checked
	 * @return whether it is a valid name for an {@link Arc}.
	 */
	public static boolean isValidArcName(String name) {
		return name.length() == 1;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof Arc;
		// Possibility: compare attributes of arc
	}
	
	@Override
	public boolean containsPoint(Vec2 point) {
		return ProofUtils.arcContainsPoint(this, point, getSize().getHeight() / 2f);
	}

	public float getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}

	public float getStopAngle() {
		return stopAngle;
	}

	public void setStopAngle(float stopAngle) {
		this.stopAngle = stopAngle;
	}

	public ArcType getArcType() {
		return arcType;
	}

	public void setArcType(ArcType arcType) {
		this.arcType = arcType;
	}
}
