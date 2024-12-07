package com.aide.ui;

import abcd.*;

import android.util.SparseArray;
import androidx.annotation.Keep;
import com.aide.ui.command.KeyStrokeCommand;
import com.aide.ui.command.MenuItemCommand;
import io.github.zeroaicy.aide.extend.OpenAideTermux;
import io.github.zeroaicy.aide.utils.FilesRenameMenu;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.aide.ui.command.FileBrowserCommand;
import com.aide.ui.command.AddToProjectCommand;

public class AppCommands {

	//空的
    private static sf[] v5 = new sf[0];
    private static sf[] Hw = new sf[0];

	//u7的重复过滤器
    private static HashSet<Class<?>> commandSet = new HashSet<>();

	//所有
    private static List<sf> allCommands = new ArrayList<>();


	//会包含在所有集合中
    private static sf[] j6;

	private static sf[] VH;
    private static sf[] Zo;
    private static sf[] FH;
    private static sf[] DW;


    private static List<KeyStrokeCommand> EQ = new ArrayList<>();
    private static List<KeyStrokeCommand> tp = new ArrayList<>();
    private static List<KeyStrokeCommand> J8 = new ArrayList<>();

    private static List<KeyStrokeCommand> we = new ArrayList<>();

	//
	private static List<KeyStrokeCommand> J0 = new ArrayList<>();

    static {
        try {
			//空的

            j6(Hw, we);
            j6(v5, J0);

			//会添加所有
            j6 = new sf[]{OpenAideTermux.getSingleton(), new lb(), new ob(), new kb(),
				new mb(), new jb(), new pc(), new cb(), new xa(), new dc(), new vc(), 
				new uc(), new tc(), new wc(), new ec(), new wb(), new ub(), new hc(), 
				new i9(), new g9(), new ca(), new bc(), new gb()};

			// 文件浏览器
			DW = new sf[]{
				// 新建项目文件 
				new s9(), new ib(), new nb(), 
				new qc(), new AddToProjectCommand(), new r9(), 
				new oa(), new oc(), new yc(), 
				new nc(), new FilesRenameMenu(), new v9(), 
				new m9(), new q9(), new p9(), 
				new ic(), new z8(), new xb(), 
				new ta(), new ua(), new qa(), 
				new sa(), new pa(), new ra(), new na()};



			FH = new sf[]{new ga(), new ia(), new fa(), 
				new ha(), new f9(), new tb(), new cc(), 
				new e9(), new n9()};


			Zo = new sf[]{new aa(), new kc(), new b9(), 
				new rc(), new j9(), new z9(), new y9(),
				new cd()};

			//
            VH = new sf[]{new c9(), new h9(), new pb(), 
				new mc(), new lc(), new hb(), new wa(), 
				new va(), new za(), new ab(), new bb(), 
				new sc(), new fc(), new ac(), new gc(), 
				new x9(), new bd(), new vb(), new u9(), 
				new l9(), new sb(), new yb(), new ka(), 
				new d9(), new w9(), new ba(), new ya(), 
				new jc(), new la(), new ja(), new rb(), 
				new ad(), new db(), new eb(), new da(), 
				new ea(), new xc(), new t9(), new o9(), 
				new qb(), new ma(), new fb(), new k9(), 
				new zc()};

			j6(Zo, EQ);
            j6(VH, EQ);

			//文件浏览器
            j6(DW, tp);

            j6(FH, J8);

			//共5类
			j6(j6, EQ);
            j6(j6, tp);
            j6(j6, J8);

			j6(j6, we);
            j6(j6, J0);

        }
		catch (Throwable th) {
        }
    }

	
	
    public AppCommands() {}

	/**
	 * API
	 */

	private static SparseArray<rf> rf = null;
	private static SparseArray<rf> rf() {
		if (AppCommands.rf == null) {
			AppCommands.rf = new SparseArray<>();
			for (sf sfVar : Hw()) {
				if (sfVar instanceof rf) {
					rf rfVar = (rf)sfVar;
					rf.put(rfVar.gn(), rfVar);
				}
			}
		}
		return AppCommands.rf;
    }
	/**
	 * 菜单
	 */
	@Keep
    public static MenuItemCommand u7(int id) {
		return get_dgSparseArray().get(id);
    }
	
	/**
	 * 菜单
	 * 寻找rf
	 */
	@Keep
    public static rf DW(int id) {
		if( u7(id) != null ){
			return null;
		}
		return rf().get(id);
    }

	private static SparseArray<MenuItemCommand> dgSparseArray;
	private static SparseArray<MenuItemCommand> get_dgSparseArray() {
		if (dgSparseArray == null) {
			dgSparseArray = new SparseArray<>();
			for (sf sfVar : Hw()) {
				if ((sfVar instanceof MenuItemCommand)) {
					MenuItemCommand menuItemCommand = (MenuItemCommand)sfVar;
					dgSparseArray.put(menuItemCommand.getMenuItemId(), menuItemCommand);
				}
			}
		}
		return dgSparseArray;
	}
	
	private static List<FileBrowserCommand> tf = null;
	
	@Keep
    public static List<FileBrowserCommand> getFileBrowserCommands() {
		if (tf == null) {
			tf = new ArrayList<>();
			for (sf sfVar : Hw()) {
				if (sfVar instanceof FileBrowserCommand) {
					tf.add((FileBrowserCommand)sfVar);
				}
			}
		}
		
		return tf;
    }
	/**
	 * 字段 Zo, VH
	 */
	@Keep
	public static List<KeyStrokeCommand> v5() {
		return EQ;
    }

	/**
	 * 所有
	 */
    @Keep
    public static List<sf> Hw() {
		return allCommands;
    }


    @Keep
    public static List<KeyStrokeCommand> VH() {
		return tp;
    }
	@Keep
	public static List<KeyStrokeCommand> gn() {
		return J8;
    }

	/**
	 * 只有字段j6
	 */
    @Keep
    public static List<KeyStrokeCommand> Zo() {
		return we;
    }
	/**
	 * 只有字段j6
	 */
    @Keep
    public static List<KeyStrokeCommand> tp() {
		return J0;
    }

    private static void j6(sf[] sfVarArr, List<KeyStrokeCommand> list) {
        for (sf sfVar : sfVarArr) {
			if (sfVar instanceof KeyStrokeCommand) {
				list.add((KeyStrokeCommand) sfVar);
			}

			if (!commandSet.contains(sfVar.getClass())) {
				commandSet.add(sfVar.getClass());
				allCommands.add(sfVar);
			}
		}
    }




}

