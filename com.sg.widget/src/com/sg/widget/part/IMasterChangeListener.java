package com.sg.widget.part;

import com.sg.db.model.CascadeObject;

public interface IMasterChangeListener {

	public void masterChanged(CascadeObject oldMaster, CascadeObject newMaster);

}
