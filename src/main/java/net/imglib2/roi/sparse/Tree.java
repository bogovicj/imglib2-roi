package net.imglib2.roi.sparse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import net.imglib2.Interval;
import net.imglib2.roi.sparse.util.BitUtils;
import net.imglib2.roi.sparse.util.DefaultInterval;
import net.imglib2.util.Intervals;

/*
 * The level of a nodeData corresponds to its height in a fully populated tree.
 * I.e, the level of a nodeData is height of the fully populated tree minus the depth of the nodeData.
 */

/**
 * A {@link SparseBitmaskNTree} with fixed dimensions. (Dimensions are specified
 * by height of the tree and dimensions of the leaf bitmasks.)
 * <p>
 * This class is not thread-safe!
 *
 * @author Tobias Pietzsch
 */
public class Tree implements SparseBitmaskNTree
{
	/**
	 * Number of dimensions.
	 */
	private final int n;

	/**
	 * How many children a inner node has ({@code 2^n}).
	 */
	private final int numChildren;

	/**
	 * Dimensions of a leaf bit-mask. <em>Every element must be a power of
	 * 2!</em>
	 */
	private final int[] leafDims;

	/**
	 * Mask out tile coordinate part from a position. Because leafDims are power
	 * of 2, {@code leafMask[ d ] = leafDims[ d ] - 1}
	 */
	private final int[] leafMask;

	/**
	 * Number of bits in a leaf bit-mask.
	 */
	private final int leafBitSize;

	/**
	 * Dimensions of a leaf bit-mask in bytes. This is the same as leafDims (the
	 * dimensions of the bitmask), except that leafDims[0] is divided by 8.
	 */
	private final int[] leafByteDims;

	/**
	 * The current height of the tree.
	 */
	private int height;

	/**
	 * The current root node.
	 */
	private NodeData root;

	/**
	 * Max of interval covered by current root node.
	 */
	private final long[] currentBoundsMax;

	/**
	 * @param leafDims
	 *            Dimensions of a leaf bit-mask. <em>Every element must be a
	 *            power of 2!</em>
	 * @param height
	 *            Initial height of the tree.
	 */
	public Tree(
			final int[] leafDims,
			final int height )
	{
		checkLeafDims( leafDims );

		n = leafDims.length;
		this.leafDims = leafDims.clone();

		leafMask = new int[ n ];
		Arrays.setAll( leafMask, d -> leafDims[ d ] - 1 );

		final long lsize = Intervals.numElements( leafDims );
		if ( lsize > Integer.MAX_VALUE )
			throw new IllegalArgumentException();
		leafBitSize = ( int ) lsize;

		leafByteDims = leafDims.clone();
		leafByteDims[ 0 ] = leafByteDims[ 0 ] >> 3;

		this.height = height;
		numChildren = 1 << n;

		root = new NodeData( null, false );

		currentBoundsMax = new long[ n ];
		updateCurrentBoundsMax();
	}

	/**
	 * Check dimensions of leaf bit-mask (for constructor).
	 * Verify that
	 * <ul>
	 *     <li>{@code leafDims} has at least one element,</li>
	 *     <li>every element is a (non-zero) power of two, and</li>
	 *     <li>{@code leafDims[0]} is at least 8.</li>
	 * </ul>
	 */
	private static void checkLeafDims( final int[] leafDims )
	{
		if ( leafDims == null || leafDims.length == 0 )
			throw new IllegalArgumentException( "leafDims must not be empty");

		for ( int i = 0; i < leafDims.length; i++ )
		{
			final int leafDim = leafDims[ i ];
			if ( leafDim < 1 )
				throw new IllegalArgumentException( "leafDim[ " + i + "] must be >= 1");
			if ( i == 0 && leafDim < 8 )
				throw new IllegalArgumentException( "leafDim[0] must be >= 8");
			if ( Integer.highestOneBit( leafDim ) != leafDim )
				throw new IllegalArgumentException( "leafDim[ " + i + "] must be a power of 2");
		}
	}

	@Override
	public int height()
	{
		return height;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	private static class NodeData
	{
		boolean value;

		BitMask data;

		NodeData parent;

		NodeData[] children;

		NodeData( final NodeData parent, final boolean value )
		{
			this.parent = parent;
			this.value = value;
		}

		boolean hasChildren()
		{
			return children != null;
		}
	}

	/**
	 * Get the lowest-level node containing position. Note that position is not
	 * necessarily the only pixel inside the node.
	 * <p>
	 * The level of a node corresponds to its height in a fully populated tree.
	 * I.e., the level of a node is the depth of the fully populated tree minus
	 * the depth of the node. For example, leaf nodes in the fully populated
	 * tree have level {@code 0}.
	 *
	 * @param pos
	 *            a position inside the image.
	 * @param maxDepth
	 *            maximum depth of the requested node. {@code maxDepth > 0}
	 *            means that the search is possibly terminated early.
	 *
	 * @return the lowest-level node containing position.
	 */
	private NodeData getNode( final long[] pos, final int maxDepth )
	{
		NodeData current = root;
		for ( int l = height - 1; l >= maxDepth; --l )
		{
			if ( !current.hasChildren() )
				break;

			int childindex = 0;
			for ( int d = 0; d < n; ++d )
			{
				final long bitmask = ( ( long ) leafDims[ d ] ) << l;
				if ( ( pos[ d ] & bitmask ) != 0 )
					childindex |= 1 << d;
			}
			current = current.children[ childindex ];
		}
		return current;
	}

	@Override
	public boolean get( final long[] pos )
	{
		final NodeData node = getNode( pos, 0 );

		if ( node.data == null )
			return node.value;

		return node.data.get( pos );
	}

	/**
	 * For debugging.
	 *
	 * returns 0 if false
	 * returns 1 if true
	 * returns 2 if mixed
	 */
	public int get( final long[] pos, final int level )
	{
		final NodeData node = getNode( pos, level );

		if ( node.hasChildren() )
			return 2;

		if ( node.data == null )
			return node.value ? 1 : 0;

		return node.data.get( pos ) ? 1 : 0;
	}

	@Override
	public void set( final long[] pos, final boolean value )
	{
		NodeData current = root;
		for ( int l = height - 1; l >= 0; --l )
		{
			if ( !current.hasChildren() )
			{
				if ( current.value == value )
					return;

				current.children = new NodeData[ numChildren ];
				for ( int i = 0; i < numChildren; ++i )
					current.children[ i ] = new NodeData( current, current.value );
			}

			int childindex = 0;
			for ( int d = 0; d < n; ++d )
			{
				final long bitmask = ( ( long ) leafDims[ d ] ) << l;
				if ( ( pos[ d ] & bitmask ) != 0 )
					childindex |= 1 << d;
			}
			current = current.children[ childindex ];
		}

		if ( current.data == null && current.value != value )
			current.data = new BitMask( current.value );

		if ( current.data != null && current.data.set( pos, value ) )
		{
			current.data = null;
			current.value = value;
			mergeUpwards( current, value );
		}
	}

	/**
	 * Create a new root, and push current root one level down. The new
	 * root will have the current root as a child at index {@code childindex}.
	 * All other children of the new root are leaf nodes with value
	 * {@code false}.
	 */
	void grow( final int childindex )
	{
		++height;
		final NodeData oldroot = root;

		root = new NodeData( null, false );
		root.children = new NodeData[ numChildren ];
		for ( int i = 0; i < numChildren; ++i )
			root.children[ i ] = ( i == childindex )
					? oldroot
					: new NodeData( root, false );
		oldroot.parent = root;
		if ( !oldroot.hasChildren() && oldroot.data == null )
			mergeUpwards( oldroot, oldroot.value );

		updateCurrentBoundsMax();
	}


	/**
	 * If all the children of our parent have the same value remove them all.
	 * Call recursively for parent.
	 *
	 * @param node
	 *            the starting node (whose parents should be tested
	 *            recursively).
	 * @return nodeData that the starting nodeData was ultimately merged into.
	 */
	private void mergeUpwards( final NodeData node, final boolean value )
	{
		final NodeData parent = node.parent;
		if ( parent == null )
			return;
		for ( int i = 0; i < numChildren; ++i )
		{
			final NodeData child = parent.children[ i ];
			if ( child.hasChildren() || child.data != null || child.value != value )
				return;
		}
		parent.value = value;
		parent.children = null;
		mergeUpwards( parent, value );
	}

	/**
	 * Bit-mask are stored in the leafs of the tree. Each bit-mask is a
	 * {@code leafDims}-sized boolean image backed by a {@code byte[]} array. It
	 * also keeps track of how many bits are currently set.
	 */
	class BitMask
	{
		/**
		 * Stores data of this mask
		 */
		private final byte[] bytes;

		/**
		 * Current number of true bits in this mask
		 */
		private int numSet;

		/**
		 * Create a new BitMask that is initially completely filled with the
		 * given value.
		 *
		 * @param initialValue
		 */
		BitMask( final boolean initialValue )
		{
			bytes = new byte[ leafBitSize >> 3 ];
			if ( initialValue )
			{
				Arrays.fill( bytes, ( byte ) 255 );
				numSet = leafBitSize;
			}
			else
			{
				numSet = 0;
			}
		}

		/**
		 * @param globalpos
		 *            global position of the bit to set
		 * @param value
		 *            value to set the bit to
		 * @return {@code true} iff the mask was completely filled or emptied by
		 *         this operation
		 */
		boolean set( final long[] globalpos, final boolean value )
		{
			final int i = byteIndex( globalpos );
			final int mask = 1 << ( globalpos[ 0 ] & leafMask[ 0 ] & 0x07 );

			final byte b = bytes[ i ];
			if ( value )
			{
				final byte bm = ( byte ) ( b | mask );
				if ( bm != b ) // changing bit from 0 to 1
				{
					bytes[ i ] = bm;
					if ( ++numSet == leafBitSize )
						return true;
				}
			}
			else
			{
				final byte bm = ( byte ) ( b & ~mask );
				if ( bm != b ) // changing bit from 1 to 0
				{
					bytes[ i ] = bm;
					if ( --numSet == 0 )
						return true;
				}
			}

			return false;
		}

		boolean get( final long[] globalpos )
		{
			final byte b = bytes[ byteIndex( globalpos ) ];
			final int mask = 1 << ( globalpos[ 0 ] & leafMask[ 0 ] & 0x07 );
			return ( b & mask ) != 0;
		}

		private int byteIndex( final long[] globalpos )
		{
			int i = 0;
			for ( int d = n - 1; d > 0; --d )
				i = ( i + ( int ) ( globalpos[ d ] & leafMask[ d ] ) ) * leafByteDims[ d - 1 ];
			return i + ( ( ( int ) ( globalpos[ 0 ] & leafMask[ 0 ] ) ) >> 3 );
		}

		int numSet()
		{
			return numSet;
		}

		/**
		 * Recompute the bounding box of true mask pixels. The result is stored
		 * in {@code bbmin}, {@code bbmax}
		 *
		 * @param bbmin
		 *            bounding box min is stored here
		 * @param bbmax
		 *            bounding box min is stored here
		 * @param tmp
		 *            temporary variable for storing positions while scanning
		 *            the mask.
		 */
		void computeBoundingBox( final int[] bbmin, final int[] bbmax, final int[] tmp )
		{
			Arrays.fill( bbmin, Integer.MAX_VALUE );
			Arrays.fill( bbmax, Integer.MIN_VALUE );
			if ( numSet == 0 )
				return;

			Arrays.fill( tmp, 0 );
			for ( int i = 0; i < bytes.length; ++i )
			{
				if ( bytes[ i ] != 0 )
				{
					for ( int d = 0; d < n; ++d )
					{
						bbmin[ d ] = Math.min( bbmin[ d ], tmp[ d ] );
						bbmax[ d ] = Math.max( bbmax[ d ], tmp[ d ] );
					}
				}
				for ( int d = 0; d < n; ++d )
				{
					if ( ++tmp[ d ] == leafByteDims[ d ] )
						tmp[ d ] = 0;
					else
						break;
				}
			}

			final int step = leafByteDims[ 0 ];

			byte minproj = 0;
			for ( int i = bbmin[ 0 ]; i < bytes.length; i += step )
				minproj |= bytes[ i ];

			byte maxproj = 0;
			if ( bbmin[ 0 ] == bbmax[ 0 ] )
				maxproj = minproj;
			else
				for ( int i = bbmax[ 0 ]; i < bytes.length; i += step )
					maxproj |= bytes[ i ];

			bbmin[ 0 ] = ( bbmin[ 0 ] << 3 ) + BitUtils.lowestOneBit( minproj );
			bbmax[ 0 ] = ( bbmax[ 0 ] << 3 ) + BitUtils.highestOneBit( maxproj );
		}
	}

	@Override
	public void forEach( final Predicate< Node > op )
	{
		final NodeIteratorImp iter = new NodeIteratorImp();
		while ( iter.hasNext() )
			if ( !op.test( iter.next() ) )
				iter.truncate();
	}

	@Override
	public NodeIterator iterator()
	{
		return new NodeIteratorImp();
	}

	/**
	 * A stack of these is used to implement NodeIterator.
	 */
	private static class NodeImp implements Node
	{
		private final int n;

		final int level;

		NodeData nodeData;

		final long[] min;

		final long[] max;

		int nextChildindex;

		private final Interval interval = new DefaultInterval()
		{
			@Override
			public long min( final int d )
			{
				return min[ d ];
			}

			@Override
			public long max( final int d )
			{
				return max[ d ];
			}

			@Override
			public long dimension( final int d )
			{
				return max[ d ] - min[ d ] + 1;
			}

			@Override
			public int numDimensions()
			{
				return n;
			}
		};

		NodeImp( final int level, final int numDimensions )
		{
			this.level = level;
			n = numDimensions;
			min = new long[ n ];
			max = new long[ n ];
		}

		@Override
		public boolean hasChildren()
		{
			return nodeData.children != null;
		}

		@Override
		public boolean value()
		{
			return nodeData.value;
		}

		@Override
		public BitMask bitmask()
		{
			return nodeData.data;
		}

		@Override
		public Interval interval()
		{
			return interval;
		}

		@Override
		public int level()
		{
			return level;
		}
	}

	private class NodeIteratorImp implements NodeIterator
	{
		private final ArrayList< NodeImp > nds;

		private NodeImp next;

		private NodeImp current;

		NodeIteratorImp()
		{
			nds = new ArrayList<>( height + 1 );
			for ( int h = 0; h <= height; ++h )
				nds.add( new NodeImp( h, n ) );
			reset();
		}

		private NodeIteratorImp( final NodeIteratorImp other )
		{
			nds = new ArrayList<>( height + 1 );
			for ( int h = 0; h <= height; ++h )
			{
				final NodeImp nd = new NodeImp( h, n );
				final NodeImp ndo = other.nds.get( h );
				nd.nodeData = ndo.nodeData;
				System.arraycopy( ndo.min, 0, nd.min, 0, n );
				System.arraycopy( ndo.max, 0, nd.max, 0, n );
				nd.nextChildindex = ndo.nextChildindex;

				nds.add( nd );

				if ( other.current == ndo )
					current = nd;
				if ( other.next == ndo )
					next = nd;
			}
		}

		@Override
		public void reset()
		{
			final NodeImp rootData = nds.get( height );
			for ( int d = 0; d < n; ++d )
			{
				rootData.min[ d ] = 0;
				final long s = ( ( long ) leafDims[ d ] ) << height;
				rootData.max[ d ] = rootData.min[ d ] + s - 1;
			}
			rootData.nodeData = root;
			rootData.nextChildindex = root.hasChildren() ? 0 : numChildren;
			next = rootData;
			current = null;
		}

		@Override
		public NodeIterator copy()
		{
			return new NodeIteratorImp( this );
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public Node next()
		{
			current = next;
			final int l = current.level;
			if ( l < height )
			{
				final NodeImp parentData = nds.get( l + 1 );
				final int i = parentData.nextChildindex++;
				current.nodeData = parentData.nodeData.children[ i ];
				current.nextChildindex = current.hasChildren() ? 0 : numChildren;
				for ( int d = 0; d < n; ++d )
				{
					final long s = ( ( long ) leafDims[ d ] ) << l;
					final long min = parentData.min[ d ] + (
							( i & ( 1 << d ) ) == 0
									? 0
									: s );
					current.min[ d ] = min;
					current.max[ d ] = min + s - 1;
				}
			}
			next = getNext( current );
			return current;
		}

		@Override
		public Node current()
		{
			return current;
		}

		void truncate()
		{
			current.nextChildindex = numChildren;
			next = getNext( current );
		}

		private NodeImp getNext( NodeImp nodeData )
		{
			while ( nodeData.nextChildindex >= numChildren )
			{
				final int l = nodeData.level;
				if ( l == height )
				{
					return null;
				}
				nodeData = nds.get( l + 1 );
			}
			return nds.get( nodeData.level - 1 );
		}
	}

	private void updateCurrentBoundsMax()
	{
		for ( int d = 0; d < n; ++d )
			currentBoundsMax[ d ] = ( leafDims[ d ] << height ) - 1;
	}

	/**
	 * The current interval covered by the tree (resp. the root node).
	 */
	private final Interval currentBounds = new DefaultInterval()
	{
		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public long dimension( final int d )
		{
			return currentBoundsMax[ d ] + 1;
		}

		@Override
		public long min( final int d )
		{
			return 0;
		}

		@Override
		public long max( final int d )
		{
			return currentBoundsMax[ d ];
		}
	};

	Interval bounds()
	{
		return currentBounds;
	}
}