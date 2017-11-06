/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2017 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.roi.geom;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealPoint;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.geom.real.ClosedEllipsoid;
import net.imglib2.roi.geom.real.ClosedSphere;
import net.imglib2.roi.geom.real.ClosedSuperEllipsoid;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.OpenSuperEllipsoid;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.geom.real.SuperEllipsoid;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link SuperEllipsoid}.
 *
 * @author Alison Walter
 */
public class SuperEllipsoidTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testOpen2DSuperEllipse()
	{
		final SuperEllipsoid< RealPoint > se = new OpenSuperEllipsoid( new double[] { 10, 10 }, new double[] { 8, 8 }, 0.5 );

		// vertices
		assertFalse( se.test( new RealPoint( new double[] { 10, 2 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 10, 18 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 2, 10 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 18, 10 } ) ) );

		// Inside ellipse
		assertTrue( se.test( new RealPoint( new double[] { 10, 10 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 3, 10 } ) ) );

		// Outside ellipse
		assertFalse( se.test( new RealPoint( new double[] { 6, 10.7 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 20, 11 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 0.5, 0 );
		assertEquals( se.center().getDoublePosition( 0 ), 10, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 10, 0 );
		assertEquals( se.semiAxisLength( 0 ), 8, 0 );
		assertEquals( se.semiAxisLength( 1 ), 8, 0 );
		assertTrue( se.boundaryType() == BoundaryType.OPEN );
	}

	@Test
	public void testClosed2DSuperEllipse()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 10, 10 }, new double[] { 8, 8 }, 0.5 );

		// vertices
		assertTrue( se.test( new RealPoint( new double[] { 10, 2 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 10, 18 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 2, 10 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 18, 10 } ) ) );

		// Inside ellipse
		assertTrue( se.test( new RealPoint( new double[] { 10, 10 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 6, 10.6 } ) ) );

		// Outside ellipse
		assertFalse( se.test( new RealPoint( new double[] { 20, 11 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 11, 15 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 0.5, 0 );
		assertEquals( se.center().getDoublePosition( 0 ), 10, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 10, 0 );
		assertEquals( se.semiAxisLength( 0 ), 8, 0 );
		assertEquals( se.semiAxisLength( 1 ), 8, 0 );
		assertTrue( se.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testOpen3DSuperEllipsoid()
	{
		final SuperEllipsoid< RealPoint > se = new OpenSuperEllipsoid( new double[] { 4, 7, 2 }, new double[] { 3, 10, 1.5 }, 6 );

		// vertices
		assertFalse( se.test( new RealPoint( new double[] { 1, 7, 2 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 7, 2 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 4, -3, 2 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 4, 17, 2 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 4, 7, 0.5 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 4, 7, 3.5 } ) ) );

		// Inside ellipse
		assertTrue( se.test( new RealPoint( new double[] { 4, 7, 2 } ) ) );

		// Outside ellipse
		assertFalse( se.test( new RealPoint( new double[] { 12, 0, 3 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 3, 13, 3.488 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 6, 0 );
		assertEquals( se.center().getDoublePosition( 0 ), 4, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 7, 0 );
		assertEquals( se.center().getDoublePosition( 2 ), 2, 0 );
		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 10, 0 );
		assertEquals( se.semiAxisLength( 2 ), 1.5, 0 );
		assertTrue( se.boundaryType() == BoundaryType.OPEN );
	}

	@Test
	public void testClosed3DSuperEllipsoid()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 4, 7, 2 }, new double[] { 3, 10, 1.5 }, 6 );

		// vertices
		assertTrue( se.test( new RealPoint( new double[] { 1, 7, 2 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7, 7, 2 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 4, -3, 2 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 4, 17, 2 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 4, 7, 0.5 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 4, 7, 3.5 } ) ) );

		// Inside ellipse
		assertTrue( se.test( new RealPoint( new double[] { 4, 7, 2 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 3, 13, 3.487 } ) ) );

		// Outside ellipse
		assertFalse( se.test( new RealPoint( new double[] { 12, 0, 3 } ) ) );

		// superellipsoid characteristics
		assertEquals( se.exponent(), 6, 0 );
		assertEquals( se.center().getDoublePosition( 0 ), 4, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 7, 0 );
		assertEquals( se.center().getDoublePosition( 2 ), 2, 0 );
		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 10, 0 );
		assertEquals( se.semiAxisLength( 2 ), 1.5, 0 );
		assertTrue( se.boundaryType() == BoundaryType.CLOSED );
	}

	@Test
	public void testMutateOpenSuperEllipsoid()
	{
		final SuperEllipsoid< RealPoint > se = new OpenSuperEllipsoid( new double[] { 2, 2 }, new double[] { 0.25, 2 }, 2 );

		assertEquals( se.exponent(), 2, 0 );
		assertEquals( se.center().getDoublePosition( 0 ), 2, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 2, 0 );
		assertEquals( se.semiAxisLength( 0 ), 0.25, 0 );
		assertEquals( se.semiAxisLength( 1 ), 2, 0 );
		assertTrue( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 7.5 } ) ) );

		// change center
		se.center().setPosition( new double[] { 5.5, 6 } );

		assertEquals( se.center().getDoublePosition( 0 ), 5.5, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 6, 0 );
		assertFalse( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 7.5 } ) ) );

		// change semi-axis length
		se.setSemiAxisLength( 0, 3 );

		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 2, 0 );
		assertFalse( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7.5, 7.5 } ) ) );

		// change exponent
		se.setExponent( 8.25 );

		assertEquals( se.exponent(), 8.25, 0 );
		assertFalse( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7.5, 7.5 } ) ) );
	}

	@Test
	public void testMutateClosedSuperEllipsoid()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 2, 2 }, new double[] { 0.25, 2 }, 2 );

		assertEquals( se.exponent(), 2, 0 );
		assertEquals( se.center().getDoublePosition( 0 ), 2, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 2, 0 );
		assertEquals( se.semiAxisLength( 0 ), 0.25, 0 );
		assertEquals( se.semiAxisLength( 1 ), 2, 0 );
		assertTrue( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 7.5 } ) ) );

		// change center
		se.center().setPosition( new double[] { 5.5, 6 } );

		assertEquals( se.center().getDoublePosition( 0 ), 5.5, 0 );
		assertEquals( se.center().getDoublePosition( 1 ), 6, 0 );
		assertFalse( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7, 7.5 } ) ) );

		// change semi-axis length
		se.setSemiAxisLength( 0, 3 );

		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 2, 0 );
		assertFalse( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertFalse( se.test( new RealPoint( new double[] { 7.5, 7.5 } ) ) );

		// change exponent
		se.setExponent( 8.25 );

		assertEquals( se.exponent(), 8.25, 0 );
		assertFalse( se.test( new RealPoint( new double[] { 2.125, 3 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 5.6, 7.5 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7, 6 } ) ) );
		assertTrue( se.test( new RealPoint( new double[] { 7.5, 7.5 } ) ) );
	}

	@Test
	public void testCenterLonger()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 0, 0, 3 }, new double[] { 3, 2 }, 2 );

		final double[] c = new double[ se.numDimensions() ];
		se.center().localize( c );
		assertEquals( c.length, se.numDimensions() );
		assertEquals( c[ 0 ], 0, 0 );
		assertEquals( c[ 1 ], 0, 0 );
	}

	@Test
	public void testSemiAxisLonger()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 0, 0 }, new double[] { 3, 4, 6 }, 2 );

		assertEquals( se.numDimensions(), 2 );
		assertEquals( se.semiAxisLength( 0 ), 3, 0 );
		assertEquals( se.semiAxisLength( 1 ), 4, 0 );

		exception.expect( IndexOutOfBoundsException.class );
		se.semiAxisLength( 2 );
	}

	@Test
	public void testNegativeSemiAxisLength()
	{
		exception.expect( IllegalArgumentException.class );
		new ClosedSuperEllipsoid( new double[] { 0, 0 }, new double[] { 3, -4 }, 5 );
	}

	@Test
	public void testNegativeExponent()
	{
		exception.expect( IllegalArgumentException.class );
		new ClosedSuperEllipsoid( new double[] { 0, 0 }, new double[] { 3, 4 }, -0.5 );
	}

	@Test
	public void testSetCenterTooLong()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 1, 2, 3 }, new double[] { 3, 2, 1 }, 3 );

		se.center().setPosition( new double[] { 3, 3, 3, 3 } );
		final double[] c = new double[ se.numDimensions() ];
		se.center().localize( c );

		assertEquals( c.length, se.numDimensions() );
		assertEquals( c[ 0 ], 3, 0 );
		assertEquals( c[ 1 ], 3, 0 );
		assertEquals( c[ 2 ], 3, 0 );
	}

	@Test
	public void testSetCenterTooShort()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 1, 2, 3 }, new double[] { 3, 2, 1 }, 3 );

		exception.expect( IndexOutOfBoundsException.class );
		se.center().setPosition( new double[] { 1, 1 } );
	}

	@Test
	public void testSetNegativeSemiAxisLength()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 1, 2, 3 }, new double[] { 3, 2, 1 }, 3 );

		exception.expect( IllegalArgumentException.class );
		se.setSemiAxisLength( 0, -0.01 );
	}

	@Test
	public void testSetZeroExponent()
	{
		final SuperEllipsoid< RealPoint > se = new ClosedSuperEllipsoid( new double[] { 1, 2, 3 }, new double[] { 3, 2, 1 }, 3 );

		exception.expect( IllegalArgumentException.class );
		se.setExponent( 0 );
	}

	@Test
	public void testBounds()
	{
		// Bounds should be the same for open and closed super ellipsoids
		final SuperEllipsoid< RealPoint > se = new OpenSuperEllipsoid( new double[] { 4, 7, 2 }, new double[] { 3, 10, 1.5 }, 6 );
		double[] min = new double[] { 4 - 3, 7 - 10, 2 - 1.5 };
		double[] max = new double[] { 4 + 3, 7 + 10, 2 + 1.5 };
		final double[] seMin = new double[ 3 ];
		final double[] seMax = new double[ 3 ];
		se.realMin( seMin );
		se.realMax( seMax );

		assertArrayEquals( min, seMin, 0 );
		assertArrayEquals( max, seMax, 0 );

		// Mutate super ellipsoid
		se.setExponent( 3 ); // this shouldn't effect bounds
		se.realMin( seMin );
		se.realMax( seMax );
		assertArrayEquals( min, seMin, 0 );
		assertArrayEquals( max, seMax, 0 );

		se.setSemiAxisLength( 1, 2 );
		min[ 1 ] = 7 - 2;
		max[ 1 ] = 7 + 2;
		se.realMin( seMin );
		se.realMax( seMax );
		assertArrayEquals( min, seMin, 0 );
		assertArrayEquals( max, seMax, 0 );

		se.center().setPosition( new double[] { 0, 0, 0 } );
		min = new double[] { -3, -2, -1.5 };
		max = new double[] { 3, 2, 1.5 };
		se.realMin( seMin );
		se.realMax( seMax );
		assertArrayEquals( min, seMin, 0 );
		assertArrayEquals( max, seMax, 0 );
	}

	@Test
	public void testEquals()
	{
		final SuperEllipsoid< RealPoint > ose = new OpenSuperEllipsoid( new double[] { 0, 0 }, new double[] { 0.5, 3 }, 4 );
		final SuperEllipsoid< RealPoint > cse = new ClosedSuperEllipsoid( new double[] { 0, 0 }, new double[] { 0.5, 3 }, 4 );
		final SuperEllipsoid< RealPoint > ose2 = new OpenSuperEllipsoid( new double[] { 0, 0 }, new double[] { 0.5, 3 }, 4 );
		final SuperEllipsoid< RealPoint > ose3 = new OpenSuperEllipsoid( new double[] { 0, 0, 0 }, new double[] { 0.5, 3, 2 }, 4 );
		final SuperEllipsoid< RealPoint > ose4 = new OpenSuperEllipsoid( new double[] { 0, 0 }, new double[] { 0.5, 3 }, 1 );

		assertTrue( ose.equals( ose2 ) );

		ose2.setSemiAxisLength( 0, 0.25 );
		assertFalse( ose.equals( ose2 ) );
		assertFalse( ose.equals( cse ) );
		assertFalse( ose.equals( ose3 ) );
		assertFalse( ose.equals( ose4 ) );

		final Sphere< RealPoint > cs = new ClosedSphere( new double[] { 10, -5, 6 }, 2.5 );
		final SuperEllipsoid< RealPoint > cse2 = new ClosedSuperEllipsoid( new double[] { 10, -5, 6 }, new double[] { 2.5, 2.5, 2.5 }, 2 );
		final Ellipsoid< RealPoint > ce = new ClosedEllipsoid( new double[] { 10, -5, 6 }, new double[] { 2.5, 2.5, 2.5 } );

		assertTrue( cse2.equals( cs ) );
		assertTrue( cse2.equals( ce ) );
	}
}
