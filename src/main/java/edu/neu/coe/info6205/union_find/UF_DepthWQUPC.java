package edu.neu.coe.info6205.union_find;

public class UF_DepthWQUPC implements UF {
	
	private int[] parent;   // parent[i] = parent of i
    private int[] depth;   // depth[i] = depth of subtree rooted at i
    private int count;  // number of components

	@Override
	public void connect(int p, int q) {
		if (!isConnected(p, q)) union(p, q);
	}

	@Override
	public int components() {
		return count;
	}

	@Override
	public int find(int p) {
		validate(p);
        while (p != parent[p])
            p = parent[p];
        return p;
	}
	
	// validate that p is a valid index
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n-1));
        }
    }

	@Override
	public void union(int p, int q) {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
