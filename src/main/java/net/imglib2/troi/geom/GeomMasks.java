package net.imglib2.troi.geom;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.troi.MaskPredicate;
import net.imglib2.troi.geom.real.Box;
import net.imglib2.troi.geom.real.ClosedBox;
import net.imglib2.troi.geom.real.ClosedEllipsoid;
import net.imglib2.troi.geom.real.ClosedPolygon2D;
import net.imglib2.troi.geom.real.ClosedSphere;
import net.imglib2.troi.geom.real.ClosedSuperEllipsoid;
import net.imglib2.troi.geom.real.DefaultLine;
import net.imglib2.troi.geom.real.DefaultPointMask;
import net.imglib2.troi.geom.real.DefaultPolygon2D;
import net.imglib2.troi.geom.real.DefaultPolyline;
import net.imglib2.troi.geom.real.DefaultRealPointCollection;
import net.imglib2.troi.geom.real.Ellipsoid;
import net.imglib2.troi.geom.real.KDTreeRealPointCollection;
import net.imglib2.troi.geom.real.Line;
import net.imglib2.troi.geom.real.NNSRealPointCollection;
import net.imglib2.troi.geom.real.OpenBox;
import net.imglib2.troi.geom.real.OpenEllipsoid;
import net.imglib2.troi.geom.real.OpenPolygon2D;
import net.imglib2.troi.geom.real.OpenSphere;
import net.imglib2.troi.geom.real.OpenSuperEllipsoid;
import net.imglib2.troi.geom.real.PointMask;
import net.imglib2.troi.geom.real.Polygon2D;
import net.imglib2.troi.geom.real.Polyline;
import net.imglib2.troi.geom.real.RealPointCollection;
import net.imglib2.troi.geom.real.RealPointSampleListRealPointCollection;
import net.imglib2.troi.geom.real.Sphere;
import net.imglib2.troi.geom.real.SuperEllipsoid;

/**
 * Utility class for creating {@link MaskPredicate}s.
 *
 * @author Alison Walter
 */
public class GeomMasks
{

	private GeomMasks()
	{
		// NB: Prevent instantiation of utility class.
	}

	// -- Box --

	/** Creates a {@link ClosedBox}. */
	public static Box< RealPoint > closedBox( final double[] min, final double[] max )
	{
		return new ClosedBox( min, max );
	}

	/** Creates an {@link OpenBox}. */
	public static Box< RealPoint > openBox( final double[] min, final double[] max )
	{
		return new OpenBox( min, max );
	}

	// -- Ellipsoid --

	/** Creates a {@link ClosedEllipsoid}. */
	public static Ellipsoid< RealPoint > closedEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new ClosedEllipsoid( center, semiAxisLengths );
	}

	/** Creates an {@link OpenEllipsoid}. */
	public static Ellipsoid< RealPoint > openEllipsoid( final double[] center, final double[] semiAxisLengths )
	{
		return new OpenEllipsoid( center, semiAxisLengths );
	}

	// -- Line --

	/** Creates a {@link DefaultLine}. */
	public static Line< RealPoint > line( final RealLocalizable pointOne, final RealLocalizable pointTwo )
	{
		return new DefaultLine( pointOne, pointTwo );
	}

	/** Creates a {@link DefaultLine}. */
	public static Line< RealPoint > line( final double[] pointOne, final double[] pointTwo, final boolean copy )
	{
		return new DefaultLine( pointOne, pointTwo, copy );
	}

	// -- Point --

	/** Creates a {@link DefaultPointMask}. */
	public static PointMask point( final double[] point )
	{
		return new DefaultPointMask( point );
	}

	/** Creates a {@link DefaultPointMask}. */
	public static PointMask point( final RealLocalizable point )
	{
		return new DefaultPointMask( point );
	}

	// -- Polygon2D --

	/** Creates a {@link DefaultPolygon2D}. */
	public static Polygon2D< RealPoint > polygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultPolygon2D( vertices );
	}

	/** Creates a {@link DefaultPolygon2D}. */
	public static Polygon2D< RealPoint > polygon2D( final double[] x, final double[] y )
	{
		return new DefaultPolygon2D( x, y );
	}

	/** Creates a {@link ClosedPolygon2D}. */
	public static Polygon2D< RealPoint > closedPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new ClosedPolygon2D( vertices );
	}

	/** Creates a {@link ClosedPolygon2D}. */
	public static Polygon2D< RealPoint > closedPolygon2D( final double[] x, final double[] y )
	{
		return new ClosedPolygon2D( x, y );
	}

	/** Creates an {@link OpenPolygon2D}. */
	public static Polygon2D< RealPoint > OpenPolygon2D( final List< ? extends RealLocalizable > vertices )
	{
		return new OpenPolygon2D( vertices );
	}

	/** Creates an {@link OpenPolygon2D}. */
	public static Polygon2D< RealPoint > OpenPolygon2D( final double[] x, final double[] y )
	{
		return new OpenPolygon2D( x, y );
	}

	// -- Polyline --

	/** Creates a {@link DefaultPolyline}. */
	public static Polyline< RealPoint > polyline( final List< ? extends RealLocalizable > vertices )
	{
		return new DefaultPolyline( vertices );
	}

	// -- RealPointCollection --

	/** Creates a {@link DefaultRealPointCollection}. */
	public static < L extends RealLocalizable > RealPointCollection< L > realPointCollection( final HashSet< L > points )
	{
		return new DefaultRealPointCollection<>( points );
	}

	/** Creates a {@link DefaultRealPointCollection}. */
	public static < L extends RealLocalizable > RealPointCollection< L > realPointCollection( final Collection< L > points )
	{
		return new DefaultRealPointCollection<>( points );
	}

	/** Creates a {@link KDTreeRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > kDTreeRealPointCollection( final KDTree< L > tree )
	{
		return new KDTreeRealPointCollection<>( tree );
	}

	/** Creates a {@link KDTreeRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > kDTreeRealPointCollection( final Collection< L > points )
	{
		return new KDTreeRealPointCollection<>( points );
	}

	/** Creates a {@link RealPointSampleListRealPointCollection}. */
	public static < L extends RealLocalizable > NNSRealPointCollection< L > realPointSampleListRealPointCollection( final RealPointSampleList< L > points )
	{
		return new RealPointSampleListRealPointCollection<>( points );
	}

	// -- Sphere --

	/** Creates a {@link ClosedSphere}. */
	public static Sphere< RealPoint > closedSphere( final double[] center, final double radius )
	{
		return new ClosedSphere( center, radius );
	}

	/** Creates an {@link OpenSphere}. */
	public static Sphere< RealPoint > OpenSphere( final double[] center, final double radius )
	{
		return new OpenSphere( center, radius );
	}

	// -- SuperEllipsoid --

	/** Creates a {@link ClosedSuperEllipsoid}. */
	public static SuperEllipsoid< RealPoint > closedSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new ClosedEllipsoid( center, semiAxisLengths );
		return new ClosedSuperEllipsoid( center, semiAxisLengths, exponent );
	}

	/** Creates an {@link OpenSuperEllipsoid}. */
	public static SuperEllipsoid< RealPoint > openSuperEllipsoid( final double[] center, final double[] semiAxisLengths, final double exponent )
	{
		if ( exponent == 2 )
			return new OpenEllipsoid( center, semiAxisLengths );
		return new OpenSuperEllipsoid( center, semiAxisLengths, exponent );
	}
}