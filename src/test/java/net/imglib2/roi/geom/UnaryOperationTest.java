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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.roi.UnaryOperation;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedBox;
import net.imglib2.roi.geom.real.OpenBox;
import net.imglib2.roi.mask.DefaultNot;
import net.imglib2.roi.mask.Mask;
import net.imglib2.roi.mask.Masks;

import org.junit.Test;

/**
 * Tests {@link UnaryOperation}s.
 *
 * @author Alison Walter
 */
public class UnaryOperationTest
{
	@Test
	public void testDefaultNot()
	{
		final Box b = new OpenBox( new double[] { 1, 1 }, new double[] { 19, 19 } );
		final Mask< RealLocalizable > rm = new DefaultNot<>( b );

		assertTrue( rm.contains( new RealPoint( new double[] { 19, 19 } ) ) );
		assertTrue( rm.contains( new RealPoint( new double[] { 111, -4 } ) ) );

		assertFalse( rm.contains( new RealPoint( new double[] { 1.1, 2 } ) ) );
		assertFalse( rm.contains( new RealPoint( new double[] { 10, 10 } ) ) );
	}

	@Test
	public void test2DRotatedBox()
	{
		final double angle = 270.0 / 180.0 * Math.PI;

		final double[][] rotationMatrix = { { Math.cos( angle ), -Math.sin( angle ) }, { Math.sin( angle ), Math.cos( angle ) } };

		final Box b = new ClosedBox( new double[] { 2.5, 1.5 }, new double[] { 6.5, 7.5 } );
		final Mask< RealLocalizable > affine = Masks.affine( b, createAffineRotationMatrix( new double[] { 4.5, 4.5 }, rotationMatrix, 2 ) );

		// Check region contains points post rotation
		// center
		assertTrue( b.contains( new RealPoint( new double[] { 4.5, 4.5 } ) ) );
		assertTrue( affine.contains( new RealPoint( new double[] { 4.5, 4.5 } ) ) );

		// Inside original rectangle but not rotated
		assertTrue( b.contains( new RealPoint( new double[] { 6.45, 7.3 } ) ) );
		assertFalse( affine.contains( new RealPoint( new double[] { 6.45, 7.3 } ) ) );

		// Inside rotated rectangle but not original
		assertFalse( b.contains( new RealPoint( new double[] { 7.3, 6.45 } ) ) );
		assertTrue( affine.contains( new RealPoint( new double[] { 7.3, 6.45 } ) ) );
	}

	@Test
	public void test3DRotatedBox()
	{
		final double angle = 30.0 / 180.0 * Math.PI;

		final double[][] rotationMatrix = { { Math.cos( angle ), 0, Math.sin( angle ) }, { 0, 1, 0 }, { -Math.sin( angle ), 0, Math.cos( angle ) } };

		final Box b = new ClosedBox( new double[] { 1, 5.75, -4 }, new double[] { 5, 8.25, 6 } );
		final Mask< RealLocalizable > affine = Masks.affine( b, createAffineRotationMatrix( new double[] { 3, 7, 1 }, rotationMatrix, 3 ) );

		// inside both
		assertTrue( b.contains( new RealPoint( new double[] { 3.5, 6.1, 2 } ) ) );
		assertTrue( affine.contains( new RealPoint( new double[] { 3.5, 6.1, 2 } ) ) );

		// inside original only
		assertTrue( b.contains( new RealPoint( new double[] { 4.99, 8, 5.93 } ) ) );
		assertFalse( affine.contains( new RealPoint( new double[] { 4.99, 8, 5.93 } ) ) );

		// inside rotated only
		assertFalse( b.contains( new RealPoint( new double[] { 7.15374953738, 8, 4.29450524066 } ) ) );
		assertTrue( affine.contains( new RealPoint( new double[] { 7.15374953738, 8, 4.29450524066 } ) ) );
	}

	@Test
	public void test2DShearedBox()
	{
		final Box b = new ClosedBox( new double[] { 1, 3 }, new double[] { 4, 9 } );
		final AffineTransform2D transform = new AffineTransform2D();
		transform.set( 1, 2, 0, 0, 1, 0 );

		final Mask< RealLocalizable > affine = Masks.affine( b, transform );

		// inside original only
		assertTrue( b.contains( new RealPoint( new double[] { 1, 9 } ) ) );
		assertFalse( affine.contains( new RealPoint( new double[] { 1, 9 } ) ) );

		// inside transformed only
		assertFalse( b.contains( new RealPoint( new double[] { 22, 9 } ) ) );
		assertTrue( affine.contains( new RealPoint( new double[] { 22, 9 } ) ) );
	}

	// -- Helper methods --

	private static AffineGet createAffineRotationMatrix( final double[] center, final double[][] rotationMatrix, final int dim )
	{
		assert rotationMatrix.length == dim;
		assert rotationMatrix[ 0 ].length == dim;

		final AffineTransform affine = new AffineTransform( dim );
		final double[][] transform = new double[ dim ][ dim + 1 ];
		assert rotationMatrix[ 0 ].length == dim;

		for ( int i = 0; i < dim; i++ )
		{
			double translate = 0;
			for ( int j = 0; j < dim + 1; j++ )
			{
				if ( i < rotationMatrix.length && j < rotationMatrix[ i ].length )
				{
					transform[ i ][ j ] = rotationMatrix[ i ][ j ];
					translate += transform[ i ][ j ] * -center[ j ];
				}
				if ( j == dim )
				{
					transform[ i ][ j ] = translate;
				}
			}
		}

		for ( int n = 0; n < dim; n++ )
		{
			transform[ n ][ dim ] += center[ n ];
		}

		affine.set( transform );
		return affine;
	}
}
