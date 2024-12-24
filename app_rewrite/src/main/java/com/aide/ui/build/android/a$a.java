//
// Decompiled by Jadx - 646ms
//
package com.aide.ui.build.android;

import abcd.hy;
import com.aide.engine.SyntaxError;
import com.aide.ui.build.android.a$a;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

class a$a implements Callable<Map<String, List<SyntaxError>>> {

    @hy
    final a DW;

    private List<a$b> j6;

	public interface TaskFactory {
		List<a$b> getTasks();
	} 
	
	private TaskFactory taskFactory;
	
    public a$a(a aVar, TaskFactory taskFactory) {
		this.DW = aVar;
		this.taskFactory = taskFactory;
    }

    @Override
    public Map<String, List<SyntaxError>> call() {
        this.j6 = this.taskFactory.getTasks();
		for (a$b bVar : this.j6) {
			String gn = bVar.gn();
			if (gn != null) {
				return a.v5(this.DW, a$b.DW(bVar), gn);
			}
		}
		return null;
    }
}

