package com.iver.cit.gvsig.export;

import java.util.ArrayList;
import java.util.List;

import com.iver.utiles.swing.threads.AbstractMonitorableTask;

public class MultiWriterTask extends AbstractMonitorableTask {
    private List<WriterTask> tasks = new ArrayList<WriterTask>();

    public void addTask(WriterTask wt) {
	tasks.add(wt);
    }

    @Override
    public void run() throws Exception {
	for (int i = 0; i < tasks.size(); i++) {
	    tasks.get(i).run();
	}
	tasks.clear();
    }

    @Override
    public void finished() {
	for (int i = 0; i < tasks.size(); i++) {
	    tasks.get(i).finished();
	}
	tasks.clear();
    }
}