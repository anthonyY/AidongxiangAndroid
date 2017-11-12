package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;



public class Table extends Entity {

	/**请求页数*/
	@JSONField(name="pa") 
	private int page = 1;
	/**
	 * 每页显示多少条数据
	 */
	@JSONField(name="li") 
	private int limit = 10;
	/**
	 * 排序方式 默认1 时间排序
	 */
	@JSONField(name="ob") 
	private int orderBy = 1;
	/**
	 * 排序顺序  1 顺序  2 倒序
	 */
	@JSONField(name="ot") 
	private int orderType = 1;
	/**
	 * 条件对象
	 */
	@JSONField(name="w") 
	private BaseWhere where;

	/**
	 * 获取页数（第几页）
	 * @return 页数（第几页）
	 */
	public int getPage() {
		return page;
	}
	/**
	 * 设置页数（第几页）
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}
	/**获取每页显示条数*/
	public int getLimit() {
		return limit;
	}
	/**
	 * 设置每页显示条数
	 * @param limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
	/**
	 * 获取排序方式
	 * @return 排序方式    默认1时间排序
	 */
	public int getOrderBy() {
		return orderBy;
	}
	/***
	 * 设置排序方式
	 * @param orderBy 
	 */
	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * 获取排序顺序  
	 * @return 排序顺序  1顺序， 2 倒序
	 */
	public int getOrderType() {
		return orderType;
	}
	/**
	 * 设置排序顺序
	 * @param orderType 排序顺序  1顺序， 2 倒序
	 */
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	/**
	 * 获取请求条件Where对象
	 * @return  Where对象
	 */
	public BaseWhere getWhere() {
		return where;
	}

	/**
	 * 设置请求条件对象
	 * @param where where对象
	 */
	public void setWhere(BaseWhere where) {
		this.where = where;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(this.page);
		dest.writeInt(this.limit);
		dest.writeInt(this.orderBy);
		dest.writeInt(this.orderType);
		dest.writeParcelable(this.where, flags);
	}

	public Table() {
	}

	protected Table(Parcel in) {
		super(in);
		this.page = in.readInt();
		this.limit = in.readInt();
		this.orderBy = in.readInt();
		this.orderType = in.readInt();
		this.where = in.readParcelable(BaseWhere.class.getClassLoader());
	}

	public static final Creator<Table> CREATOR = new Creator<Table>() {
		@Override
		public Table createFromParcel(Parcel source) {
			return new Table(source);
		}

		@Override
		public Table[] newArray(int size) {
			return new Table[size];
		}
	};
}
