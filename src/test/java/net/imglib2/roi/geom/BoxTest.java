/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealPoint;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedBox;
import net.imglib2.roi.geom.real.OpenBox;

import org.junit.Test;

/**
 * Tests for {@link Box}.
 *
 * @author Alison Walter
 */
public class BoxTest
{
	@Test
	public void testTwoDimensionalOpenRectangle()
	{
		final Box b = new OpenBox( new double[] { -6.8, -3.2375 }, new double[] { 13.2, 3.2625 });

		// vertices
		assertFalse( b.contains( new RealPoint( new double[] { -6.8, -3.2375 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { -6.8, 3.2625 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { 13.2, -3.2375 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { 13.2, 3.2625 } ) ) );

		// inside
		assertTrue( b.contains( new RealPoint( new double[] { 0, 0 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { 9, 1.625 } ) ) );

		// outside
		assertFalse( b.contains( new RealPoint( new double[] { -6.8, 3.25 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { 13.2, 0 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { 11, 3.2625 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { -4, -3.2375 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { 12, 20 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { -6.81, 0 } ) ) );

		// box characteristics
		assertEquals( b.sideLength( 0 ), 20, 0 );
		assertEquals( b.sideLength( 1 ), 6.5, 0 );
		assertEquals( b.center()[ 0 ], 3.2, 1e-15 );
		assertEquals( b.center()[ 1 ], 0.0125, 1e-15 );
	}

	@Test
	public void testTwoDimensionalClosedRectangle()
	{
		final Box b = new ClosedBox( new double[] { -6.8, -3.2375 }, new double[] { 13.2, 3.2625 });

		// vertices
		assertTrue( b.contains( new RealPoint( new double[] { -6.8, -3.2375 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { -6.8, 3.2625 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { 13.2, -3.2375 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { 13.2, 3.2625 } ) ) );

		// inside
		assertTrue( b.contains( new RealPoint( new double[] { -6.8, 3.25 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { 13.2, 0 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { 11, 3.2625 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { -4, -3.2375 } ) ) );
		assertTrue( b.contains( new RealPoint( new double[] { 0, 0 } ) ) );

		// outside
		assertFalse( b.contains( new RealPoint( new double[] { 12, 20 } ) ) );
		assertFalse( b.contains( new RealPoint( new double[] { -6.81, 0 } ) ) );

		// box characteristics
		assertEquals( b.sideLength( 0 ), 20, 0 );
		assertEquals( b.sideLength( 1 ), 6.5, 0 );
		assertEquals( b.center()[ 0 ], 3.2, 1e-15 );
		assertEquals( b.center()[ 1 ], 0.0125, 1e-15 );
	}

	@Test
	public void testHighDimensionalOpenBox()
	{
		final Box hc = new OpenBox( new double[] { 3, 3, 3, 3 }, new double[] { 7, 7, 7, 7 });

		// vertices
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 3, 3, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 3, 3, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 3, 7, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 3, 7, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 7, 3, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 7, 3, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 7, 7, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 7, 7, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 3, 3, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 3, 3, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 3, 7, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 3, 7, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 7, 3, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 7, 3, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 7, 7, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 7, 7, 7 } ) ) );

		// should contain:
		assertTrue( hc.contains( new RealPoint( new double[] { 4, 4, 4, 4 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 5, 6, 4, 5 } ) ) );

		// should not contain:
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 3, 5, 4 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 7, 7, 4, 4 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 6, 5, 5 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 6, 6, 5, 7 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 2, 3, 3, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 2, 3, 3 } ) ) );

		// box characteristics
		assertEquals( hc.sideLength( 0 ), 4, 0 );
		assertEquals( hc.sideLength( 1 ), 4, 0 );
		assertEquals( hc.sideLength( 2 ), 4, 0 );
		assertEquals( hc.sideLength( 3 ), 4, 0 );
		assertEquals( hc.center()[ 0 ], 5, 0 );
		assertEquals( hc.center()[ 1 ], 5, 0 );
		assertEquals( hc.center()[ 2 ], 5, 0 );
		assertEquals( hc.center()[ 3 ], 5, 0 );
	}

	@Test
	public void testHighDimensionalClosedBox()
	{
		final Box hc = new ClosedBox( new double[] { 3, 3, 3, 3 }, new double[] { 7, 7, 7, 7 });

		// vertices
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 3, 3, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 3, 3, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 3, 7, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 3, 7, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 7, 3, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 7, 3, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 7, 7, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 7, 7, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 3, 3, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 3, 3, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 3, 7, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 3, 7, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 7, 3, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 7, 3, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 7, 7, 3 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 7, 7, 7 } ) ) );

		// should contain:
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 3, 5, 4 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 7, 7, 4, 4 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 3, 6, 5, 5 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 6, 6, 5, 7 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 4, 4, 4, 4 } ) ) );
		assertTrue( hc.contains( new RealPoint( new double[] { 5, 6, 4, 5 } ) ) );

		// should not contain:
		assertFalse( hc.contains( new RealPoint( new double[] { 2, 3, 3, 3 } ) ) );
		assertFalse( hc.contains( new RealPoint( new double[] { 3, 2, 3, 3 } ) ) );

		// box characteristics
		assertEquals( hc.sideLength( 0 ), 4, 0 );
		assertEquals( hc.sideLength( 1 ), 4, 0 );
		assertEquals( hc.sideLength( 2 ), 4, 0 );
		assertEquals( hc.sideLength( 3 ), 4, 0 );
		assertEquals( hc.center()[ 0 ], 5, 0 );
		assertEquals( hc.center()[ 1 ], 5, 0 );
		assertEquals( hc.center()[ 2 ], 5, 0 );
		assertEquals( hc.center()[ 3 ], 5, 0 );
	}
}
