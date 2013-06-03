package com.sg.db.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sg.db.DBActivator;
import com.sg.db.Util;
import com.sg.db.expression.IConfConstants;
import com.sg.db.expression.query.QueryExpression;

public class CascadeObject extends SingleObject {

	private CascadeObject parent;

	public static final String CASCADE = "CASCADE";

	// private DBCollection collection;

	private String cascadeContext;

	private IConfigurationElement ce;

	private List<CascadeObject> children;

	private Map<String, SingleObject> ext;// 横向展开的节点

	private Map<String, String> parameters = new HashMap<String, String>();

	private Map<String, Object> internalParameters = new HashMap<String, Object>();

	private boolean masterDataLoaded = false;

	private boolean childrenLoaded = false;

	private boolean extensionDataLoaded = false;

	private String configurationElementId;

	private String _skip;

	private String _limit;

	private String _returnFieldNameList;

	private String _sortFieldNameList;

	/**
	 * 根据配置文件进行实例化
	 * 
	 * @param ce
	 */
	public CascadeObject(IConfigurationElement ce) {
		super();
		configurationElementId = ce.getAttribute(IConfConstants.ATT_ID);
		cascadeContext = ce.getAttribute(IConfConstants.ATT_CASCADECONTEXT);
		this.ce = ce;
	}

	/**
	 * 传入一个配置文件的id
	 * 
	 * @param id
	 */
	public CascadeObject(String configurationElementId) {
		super();
		if (configurationElementId != null) {
			this.configurationElementId = configurationElementId;
			CascadeObject def = DBActivator.getCascadeObject(configurationElementId);
			this.ce = def.ce;
			cascadeContext = ce.getAttribute(IConfConstants.ATT_CASCADECONTEXT);

			String collectionName = ce.getAttribute(IConfConstants.ATT_COLLECTION);
			if (!Util.isNullorEmpty(collectionName)) {
				DBCollection coll = DBActivator.getDefaultDBCollection(collectionName);
				if (coll != null)
					setCollection(coll);
			}
		}
	}

	/**
	 */
	public CascadeObject(String configurationElementId, DBObject dbobject) {
		this(configurationElementId);
		setData(dbobject);
	}

	/**
	 * 在load以前调用，设置singleValue的取值的参数值
	 * 
	 * @param parameters
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getConfigurationElementId() {
		return configurationElementId;
	}

	public String getCascadeContext() {
		return cascadeContext;
	}

	public DBObject getData() {
		return dbObject;
	}

	public CascadeObject setData(DBObject dbo) {
		dbObject = dbo;
		masterDataLoaded = true;
		return this;
	}

	public CascadeObject getParent() {
		return this.parent;
	}

	public List<CascadeObject> getChildren() {
		if (!childrenLoaded) {
			loadChildren();
		}
		return children;
	}

	public List<CascadeObject> loadChildren() {

		initData();

		children = new ArrayList<CascadeObject>();

		if (ce != null) {
			// 首先取出传递来的参数
			setInternalParametersFromMasterData(ce);

			createChildrenByDefinition(ce);

		}

		childrenLoaded = true;
		return children;
	}

	private void createChildrenByDefinition(IConfigurationElement ceParent) {
		IConfigurationElement[] ces = ceParent.getChildren(IConfConstants.ELEMENT_NAME_LISTOBJECT);
		for (IConfigurationElement listObjects : ces) {
			// 使用ChildrenProvider
			boolean continueGet = createChildrenByProvider(listObjects);// 如果不希望再通过定义取子节点，返回false
			if (!continueGet)
				continue;

			// 使用SingleObjectDefinition以及queryexpressiondefinition
			IConfigurationElement[] listObject = listObjects.getChildren();
			for (IConfigurationElement ceChild : listObject) {
				String name = ceChild.getName();

				if (IConfConstants.ELEMENT_NAME_QUERYT_DEF.equals(name)) {
					String expName = ceChild.getAttribute(IConfConstants.ATT_QUERY_EXPRESSION);
					// 这个配置不要为空
					Assert.isTrue(!Util.isNullorEmpty(expName), "ID: " + getConfigurationElementId() + ", "
							+ IConfConstants.ELEMENT_NAME_SINGLEOBJECT_DEF + " need point to an element id.");

					QueryExpression exp = (QueryExpression) DBActivator.getExpression(expName);

					Assert.isTrue(!Util.isNullorEmpty(expName), "ID: " + getConfigurationElementId() + ", "
							+ IConfConstants.ELEMENT_NAME_SINGLEOBJECT_DEF + " need point to an element id.");

					exp.passParamValueMap(internalParameters);

					// **************************zhonghua 2012/3/16 接受处理外部的设置
					setRootInputSetting(exp, ceChild);
					// **************************zhonghua 2012/3/16 接受处理外部的设置

					DBCursor cur = exp.run();

					if (cur != null) {
						String singleObjectDefinitionId = ceChild.getAttribute(IConfConstants.ATT_SINGLEOBJECT_ID);
						String collectionName = ceChild.getAttribute(IConfConstants.ATT_COLLECTION);
						DBCollection coll = null;
						if (!Util.isNullorEmpty(collectionName)) {
							coll = DBActivator.getDefaultDBCollection(collectionName);
						}
						while (cur.hasNext()) {
							DBObject dbobject = cur.next();

							createChild(singleObjectDefinitionId, dbobject, coll);

						}
					}
				} else if (IConfConstants.ELEMENT_NAME_SINGLEOBJECT_DEF.equals(name)) {
					String singleObjectDefinitionId = ceChild.getAttribute(IConfConstants.ATT_SINGLEOBJECT_ID);
					// 这个配置不要为空
					Assert.isTrue(!Util.isNullorEmpty(singleObjectDefinitionId), "ID: " + getConfigurationElementId() + ", "
							+ IConfConstants.ELEMENT_NAME_SINGLEOBJECT_DEF + " need point to an element id.");
					// 为这个对象传递参数
					createChild(singleObjectDefinitionId);
				}

			}

		}

	}

	public CascadeObject createChild(String singleObjectDefinitionId) {
		CascadeObject co = new CascadeObject(singleObjectDefinitionId);
		co.internalParameters = internalParameters;
		// 取出接受singleObjectDefinitionId对应singleObjectDefinitionId的接受参数
		// TODO Auto-generated method stub
		// **************************zhonghua 2012/3/16
		// 接受处理外部的设置传递到下一级
		passRootInputSetting(co);
		// **************************zhonghua 2012/3/16
		// 接受处理外部的设置
		co.parent = this;

		if (children == null)
			children = new ArrayList<CascadeObject>();
		children.add(co);
		return co;
	}

	public CascadeObject createChild(String singleObjectDefinitionId, DBObject dbobject, DBCollection coll) {
		CascadeObject co = new CascadeObject(singleObjectDefinitionId, dbobject);
		co.setCollection(coll);

		// **************************zhonghua 2012/3/16
		// 接受处理外部的设置传递到下一级
		passRootInputSetting(co);
		// **************************zhonghua 2012/3/16
		// 接受处理外部的设置
		co.parent = this;

		if (children == null)
			children = new ArrayList<CascadeObject>();
		children.add(co);
		return co;
	}

	private void passRootInputSetting(CascadeObject child) {
		child.setSkipAndLimit(_skip, _limit);
		child.setReturnFieldsFromString(_returnFieldNameList);
		child.setSortFieldsFromString(_sortFieldNameList);
		child.setParamValueMap(parameters);
	}

	private void setRootInputSetting(QueryExpression exp, IConfigurationElement ce) {
		if (accept(ce, IConfConstants.ATT_ACCEPT_SORT)) {
			exp.setSortFieldsFromString(_sortFieldNameList);
		}

		if (accept(ce, IConfConstants.ATT_ACCEPT_PARAMETER)) {
			exp.setParamValueMap(parameters);
		}

		if (accept(ce, IConfConstants.ATT_ACCEPT_RETURNFIELDS)) {
			exp.setReturnFieldsFromString(_returnFieldNameList);
		}

		if (accept(ce, IConfConstants.ATT_ACCEPT_SKIPNLIMIT)) {
			exp.setSkipAndLimit(_skip, _limit);
		}
	}

	private boolean accept(IConfigurationElement ce, String attAccept) {
		return "true".equals(ce.getAttribute(attAccept));
	}

	public void setCollection(DBCollection coll) {
		this.collection = coll;
	}

	private boolean createChildrenByProvider(IConfigurationElement ceParent) {
		try {
			IChildrenProvider cp = (IChildrenProvider) ceParent.createExecutableExtension(IConfConstants.ATT_CHILDREN_PROVIDER);
			children.addAll(cp.getChildren(this));
			return cp.continueGetChildrenFromDefinition();
		} catch (CoreException e) {
		}
		return true;
	}

	/**
	 * 根据参数关联设置参数传递的定义
	 * 
	 * @param ceParent
	 */
	private void setInternalParametersFromMasterData(IConfigurationElement ceParent) {
		if (dbObject != null) {
			IConfigurationElement[] ces = ceParent.getChildren(IConfConstants.ELEMENT_NAME_PARAMETER_EVA);
			if (ces == null) {
				return;
			}
			for (IConfigurationElement ce : ces) {
				String from = ce.getAttribute(IConfConstants.ATT_FROM_WHICH_KEY);
				Object value = dbObject.get(from);
				String to = ce.getAttribute(IConfConstants.ATT_TO_WHICH_PARAMETER);
				internalParameters.put(to, value);
			}
		}
	}

	private DBObject loadDataObject() {
		// 有三种加载方法，按照以下的优先级
		// 第一种，传入collectionName 和ObjectId
		if (collection != null) {
			String oid = ce.getAttribute(IConfConstants.ATT_OBJECTID);
			if (!Util.isNullorEmpty(oid)) {
				ObjectId objectId = new ObjectId(oid);
				return collection.findOne(objectId);
			}
		}
		// 第二种，使用dataProvider接口传入
		try {
			IDBObjectProvider dataProvider = (IDBObjectProvider) ce.createExecutableExtension(IConfConstants.ATT_DATAPROVIDER);
			if (dataProvider != null) {
				return dataProvider.getDBObject();
			}
		} catch (CoreException e) {
		}
		// 第三种，使用key_valueMap
		IConfigurationElement[] ces = ce.getChildren(IConfConstants.ATT_KEY_VALUE);
		BasicDBObject dbObject = new BasicDBObject();

		if (ces != null && ces.length > 0) {
			for (int i = 0; i < ces.length; i++) {
				String key = ces[i].getAttribute(IConfConstants.ATT_FIELD_NAME);
				Object value = createValueObject(ces[i]);
				dbObject.put(key, value);
			}

		}
		return dbObject;
	}

	private Object createValueObject(IConfigurationElement ce) {
		IConfigurationElement[] ces = ce.getChildren(IConfConstants.ATT_SINGLE_VALUE);
		if (ces != null && ces.length > 0) {
			return createSingleValueObject(ces[0]);
		}
		return null;
	}

	private Object createSingleValueObject(IConfigurationElement ce) {

		String parameterName = ce.getAttribute(IConfConstants.ATT_PARAMETERNAME);
		if ("".equals(parameterName) || (parameterName == null)) {
			String inputValue = ce.getAttribute(IConfConstants.ATT_VALUE);
			if (inputValue == null || inputValue.equals("")) {
				return null;
			} else {
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		} else {
			// 先从内部参数映射表中查找
			if (internalParameters.keySet().contains(parameterName)) {
				return internalParameters.get(parameterName);
			} else if (parameters.keySet().contains(parameterName)) {// 然后从外部参数表中查找
				String inputValue = parameters.get(parameterName);
				String type = ce.getAttribute(IConfConstants.ATT_VALUE_TYPE);
				return Util.getTypeValue(inputValue, type);
			}
		}
		return null;
	}

	public int getChildrenCount() {
		getChildren();
		return children.size();
	}

	@Override
	public DBCollection getCollection() {
		return collection;
	}

	@Override
	public boolean remove() {
		collection.remove(getData());
		parent.removeChild(this);
		fireEvent(REMOVE);
		return true;
	}

	public boolean remove(boolean cascadeRemove) {
		if (cascadeRemove) {
			List<CascadeObject> ls = new ArrayList<CascadeObject>();
			ls.addAll(getChildren());
			for (int i = 0; i < ls.size(); i++) {
				ls.get(i).remove(cascadeRemove);
			}
		}

		remove();
		fireEvent(REMOVE);
		return true;
	}

	@Override
	public boolean save() {
		if (isNewObject()) {
			ObjectId oid = new ObjectId();
			dbObject.put("_id", oid);
			collection.insert(dbObject);
			fireEvent(INSERTED);
		} else {
			collection.save(dbObject);
			fireEvent(UPDATED);
		}
		saveExtObjects();
		return true;
	}

	public boolean save(String extName) {
		if (extName == null) {
			return save();
		} else {
			SingleObject so = ext.get(extName);
			return so.save();
		}
	}

	private void saveExtObjects() {
		if (ext != null) {
			Iterator<SingleObject> iter = ext.values().iterator();
			while (iter.hasNext()) {
				SingleObject so = iter.next();
				so.save();
			}
		}
	}

	@Override
	public void initData() {
		if (!masterDataLoaded) {
			dbObject = loadDataObject();
			masterDataLoaded = true;
		}
		if (!extensionDataLoaded) {
			loadExtensionObjects();
			extensionDataLoaded = true;
		}
	}

	private Map<String, SingleObject> loadExtensionObjects() {

		ext = new HashMap<String, SingleObject>();

		if (ce != null) {
			// 首先取出传递来的参数
			setInternalParametersFromMasterData(ce);

			// 使用ChildrenProvider

			IConfigurationElement[] ces = ce.getChildren(IConfConstants.ELEMENT_NAME_EXTOBJECT);
			for (IConfigurationElement listObjects : ces) {

				String fieldPrefix = listObjects.getAttribute(IConfConstants.ATT_FIELD_NAME_PREFIX);

				boolean continueGet = createChildrenByProvider(listObjects);// 如果不希望再通过定义取子节点，返回false
				if (!continueGet)
					continue;

				IConfigurationElement[] listObject = listObjects.getChildren();
				for (IConfigurationElement ceChild : listObject) {
					String name = ceChild.getName();

					if (IConfConstants.ELEMENT_NAME_QUERYT_DEF.equals(name)) {
						String expName = ceChild.getAttribute(IConfConstants.ATT_QUERY_EXPRESSION);
						// 这个配置不要为空
						Assert.isTrue(!Util.isNullorEmpty(expName), "ID: " + getConfigurationElementId() + ", "
								+ IConfConstants.ELEMENT_NAME_SINGLEOBJECT_DEF + " need point to an element id.");

						QueryExpression exp = (QueryExpression) DBActivator.getExpression(expName);

						exp.passParamValueMap(internalParameters);

						setRootInputSetting(exp, ceChild);

						DBCursor cur = exp.run();

						if (cur != null && cur.hasNext()) {
							String collectionName = ceChild.getAttribute(IConfConstants.ATT_COLLECTION);
							DBCollection coll = null;
							if (!Util.isNullorEmpty(collectionName)) {
								coll = DBActivator.getDefaultDBCollection(collectionName);
							}

							SingleObject co = new SingleObject();
							co.setData(cur.next());
							co.setCollection(coll);

							ext.put(fieldPrefix, co);
						}
					}

				}

			}

		}

		return ext;
	}

	public void rootReload() {
		masterDataLoaded = false;
		childrenLoaded = false;
		loadChildren();
	}

	@Override
	public DBObject getBSONResult(Map<String, String> transferFields, Set<String> removeFields) {
		initData();
		DBObject dbo = super.getBSONResult(transferFields, removeFields);

		if (ext != null) {
			Iterator<String> iter = ext.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				SingleObject so = ext.get(key);
				if (so != null) {
					DBObject extdbo = Util.translateBSON(so.getData(), key, transferFields, removeFields);
					dbo.putAll(extdbo);
				}
			}
		}

		List<CascadeObject> cl = getChildren();

		if (cl.size() > 0) {

			BasicDBList dbolist = new BasicDBList();

			for (CascadeObject co : cl) {
				dbolist.add(co.getBSONResult(transferFields, removeFields));
			}

			dbo.put(CASCADE, dbolist);
		}

		return dbo;
	}

	/**
	 * @param skip
	 * @param limit
	 */
	public void setSkipAndLimit(String skip, String limit) {
		_skip = skip;
		_limit = limit;
	}

	/**
	 * @param returnFieldsNameList
	 */
	public void setReturnFieldsFromString(String returnFieldsNameList) {
		_returnFieldNameList = returnFieldsNameList;
	}

	/**
	 * @param sortFieldNameList
	 */
	public void setSortFieldsFromString(String sortFieldNameList) {
		_sortFieldNameList = sortFieldNameList;
	}

	/**
	 * 
	 * @param inputParaMap
	 */
	public void setParamValueMap(Map<String, String> inputParaMap) {
		parameters = inputParaMap;
	}

	public CascadeObject appendParamValue(String key, String value) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		parameters.put(key, value);
		return this;
	}

	public void passParamValueMap(Map<String, Object> valueMap) {
		internalParameters = valueMap;
	}

	public CascadeObject setParamValue(String key, Object value) {
		if (internalParameters == null) {
			internalParameters = new HashMap<String, Object>();
		}
		internalParameters.put(key, value);
		return this;
	}

	/**
	 * 修改这个方法使他能够支持 读取ext中的数据
	 */
	@Override
	public Object getValue(String key) {
		if (key.contains(".")) {// 要从extension中取
			initData();
			return getExtValue(key);
		} else {// 从master中取
			return super.getValue(key);
		}
	}

	@Override
	public void setValue(String key, Object newValue, Object source, boolean noticeFieldValueChange) {
		if (key.contains(".")) {// 要从extension中取
			setExtValue(key, newValue, source, noticeFieldValueChange);
		} else {
			super.setValue(key, newValue, source, noticeFieldValueChange);
		}
	}

	public void setValue(String key, Object newValue) {
		setValue(key, newValue, null, false);
	}

	private void setExtValue(String key, Object newValue, Object source, boolean noticeFieldValueChange) {
		String[] str = Util.split(key, ".");
		Assert.isNotNull(str, "prefix IS NULL!");
		Assert.isNotNull(str[0], "prefix IS NULL!");
		Assert.isNotNull(str[1], "prefix key has no data!");

		SingleObject so = ext.get(str[0]);
		if (so != null)
			so.setValue(str[1], newValue, source, noticeFieldValueChange);
	}

	private Object getExtValue(String key) {
		String[] str = Util.split(key, ".");
		Assert.isNotNull(str, "prefix IS NULL!");
		Assert.isNotNull(str[0], "prefix IS NULL!");
		Assert.isNotNull(str[1], "prefix key has no data!");

		SingleObject so = ext.get(str[0]);
		if (so != null)
			return so.getValue(str[1]);
		return null;
	}

	public CascadeObject getUpNeighbor() {
		if (parent == null) {
			return null;
		}

		List<CascadeObject> list = parent.getChildren();

		int index = list.indexOf(this);
		if (index > 0) {
			return list.get(index - 1);
		} else {
			return null;
		}
	}

	public CascadeObject getDownNeighbor() {
		if (parent == null) {
			return null;
		}

		List<CascadeObject> list = parent.getChildren();

		int index = list.indexOf(this);
		if (index < (list.size() - 1)) {
			return list.get(index + 1);
		} else {
			return null;
		}
	}

	public void sortChildren(String[] keys) {
		if (children != null) {
			Collections.sort(children, new SingleObjectComparator(keys));
		}
	}

	public void sortChildren(Comparator<CascadeObject> sorter) {
		if (children != null) {
			Collections.sort(children, sorter);
		}
	}

	public void addChild(CascadeObject child) {
		if (children == null) {
			childrenLoaded = true;
			children = new ArrayList<CascadeObject>();
		}
		child.parent = this;
		children.add(child);
	}

	public void addChild(CascadeObject child, int i) {
		if (children == null) {
			childrenLoaded = true;
			children = new ArrayList<CascadeObject>();
		}
		child.parent = this;
		children.add(i, child);
	}

	public void removeChild(CascadeObject child) {
		if (children != null) {
			// child.parent = null;
			children.remove(child);
		}
	}

	public CascadeObject getRoot() {
		CascadeObject object = this;
		while(object.getParent()!=null){
			object = object.getParent();
		}
		return object;
	}

}
