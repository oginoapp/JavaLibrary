package example;

import java.util.ArrayList;
import java.util.List;

/**
 * 2分探索木
 */
public class BinaryNode {

	private Integer value;

	private BinaryNode left;

	private BinaryNode right;

	/**
	 * データ追加
	 */
	public void add(int value) {
		if(this.value == null) {
			this.value = value;
		} else if(value >= this.value) {
			//右に渡す
			if(this.right == null) {
				this.right = new BinaryNode();
			}
			this.right.add(value);
		} else {
			//左に渡す
			if(this.left == null) {
				this.left = new BinaryNode();
			}
			this.left.add(value);
		}
	}

	/**
	 * 探索
	 */
	public boolean contains(int value) {
		if(this.value == value) {
			return true;
		} else if(value >= this.value) {
			if(this.right != null) {
				return this.right.contains(value);
			}
		} else {
			if(this.left != null) {
				return this.left.contains(value);
			}
		}
		return false;
	}

	/**
	 * 昇順にソートしたリストを戻す
	 */
	public List<Integer> getSortedList() {
		List<Integer> sortedList = new ArrayList<>();
		if(this.left != null) {
			sortedList.addAll(this.left.getSortedList());
		}
		sortedList.add(this.value);
		if(this.right != null) {
			sortedList.addAll(this.right.getSortedList());
		}
		return sortedList;
	}

}


