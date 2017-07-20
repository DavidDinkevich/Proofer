package geometry.proofs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import util.Utils;

public class TriangleFigure extends SimpleFigure {
	private SegmentFigure[] segs;
	private AngleFigure[] angles;
	private List<Figure> children;
	
	public TriangleFigure(String name) {
		setName(name);
		segs = new SegmentFigure[3];
		initSegments();
		angles = new AngleFigure[3];
		initAngles();
	}
	public TriangleFigure() {
		this("\0\0\0");
	}
	
	private void initSegments() {
		for (int i = 0; i < segs.length; i++)
			segs[i] = new SegmentFigure();
		renameSegments();
	}
	
	private void initAngles() {
		for (int i = 0; i < angles.length; i++)
			angles[i] = new AngleFigure();
		renameAngles();
	}
	
	private void renameSegments() {
		if (segs == null)
			return;
		segs[0].setName(getName().substring(0, 2));
		segs[1].setName(getName().substring(1, 3));
		String s = String.valueOf(getName().charAt(0)) + String.valueOf(getName().charAt(2));
		segs[2].setName(s);
	}
	
	private void renameAngles() {
		if (angles == null)
			return;
		String c0 = String.valueOf(getName().charAt(0));
		String c1 = String.valueOf(getName().charAt(1));
		String c2 = String.valueOf(getName().charAt(2));
		
		angles[0].setName(c1 + c0 + c2);
		angles[1].setName(getName()); // c1 + c2 + c3
		angles[2].setName(c1 + c2 + c0);			
	}
	
	private SegmentFigure getSegment(String name) {
		if (name.length() != 2)
			return null;
		for (SegmentFigure s : segs) {
			if (s.isValidName(name))
				return s;
		}
		return null;
	}
	
	private AngleFigure getAngle(String name) {			
		if (name.length() == 1 || name.length() == 3) {
			for (AngleFigure a : angles) {
				if (name.length() == 1) {
					if (a.getNameShort().equals(name))
						return a;
				}
				else if (name.length() == 3) {
					if (a.isValidName(name))
						return a;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && o instanceof TriangleFigure;
	}
	
	@Override
	public void setName(String name) {
		super.setName(Utils.mergeStringsAndEnsureCapacity(3, 3, getName(), name));
		renameSegments();
		renameAngles();
	}
	
	@Override
	public boolean isValidName(String name) {
		return new AngleFigure(getName()).isValidName(name);
	}
	
	@Override
	public Figure getChild(String name) {
		SegmentFigure seg = getSegment(name);
		if (seg != null)
			return seg;
		return getAngle(name);
	}
	
	@Override
	public List<Figure> getChildren() {
		if (children == null) {
			children = new ArrayList<>();
			children.addAll(Arrays.asList(segs));
			children.addAll(Arrays.asList(angles));
		}
		return Collections.unmodifiableList(children);
	}

	public SegmentFigure[] getSegmentsAdjacentToAngle(String angleName) {
		AngleFigure a;
		if ((a = getAngle(angleName)) == null)
			return null;
		return new SegmentFigure[] {
				getSegment(a.getName().substring(0, 2)),
				getSegment(a.getName().substring(1))
		};
	}
	
	public SegmentFigure[] getSegments() {
		return segs;
	}
	
	public AngleFigure[] getAngles() {
		return angles;
	}
}