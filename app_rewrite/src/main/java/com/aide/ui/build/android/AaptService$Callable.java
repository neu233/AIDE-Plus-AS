//
// Decompiled by Jadx - 812ms
//
package com.aide.ui.build.android;

import com.aide.common.AppLog;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

class AaptService$Callable implements Callable<AaptService$ErrorResult> {
    final AaptService aaptService;
	private TaskFactory taskFactory;

	public interface TaskFactory {
		List<AaptService$Task> getTasks( );
	} 

    public AaptService$Callable( AaptService aaptService, TaskFactory taskFactory ) {
        try {
            this.aaptService = aaptService;
            this.taskFactory = taskFactory;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    @Override
    public AaptService$ErrorResult call( ) {
        try {
			List<AaptService$Task> tasks = taskFactory.getTasks();

            boolean hasError = false;
            for ( AaptService$Task task : tasks ) {
                AaptService$ErrorResult taskErrorResult = task.runTask();

				hasError |= taskErrorResult.hasError;

                if ( taskErrorResult.errorInfo != null ) {
                    String mainProjectPath = AaptService$Task.getMainProjectPath(task);
					
					Map<String, String> androidManifestMap = AaptService$Task.getAndroidManifestMap(task);
					
					taskErrorResult.syntaxErrorsMap = AaptService.resolvingError(this.aaptService, mainProjectPath, androidManifestMap, taskErrorResult.errorInfo);
                    AppLog.d(taskErrorResult.syntaxErrorsMap.toString());
					return taskErrorResult;
                }
            }
			
            return new AaptService$ErrorResult(hasError);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}

