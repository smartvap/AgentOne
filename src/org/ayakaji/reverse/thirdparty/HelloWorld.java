package org.ayakaji.reverse.thirdparty;

import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public class HelloWorld {
	public interface CLibrary extends Library {
		CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);
		void printf(String format, Object... args);
	}

	public interface Kernel32 extends StdCallLibrary {

		public class SystemTime extends Structure {
			public short wYear;
			public short wMonth;
			public short wDayOfWeek;
			public short wDay;
			public short wHour;
			public short wMinute;
			public short wSecond;
			public short wMilliseconds;

			@Override
			protected List<?> getFieldOrder() {
				return null;
			}
		}

		void GetLocalTime(SystemTime result);
	}

	public static void main(String[] args) {
		CLibrary.INSTANCE.printf("Hello, World\n");
		System.out.println("map $request_uri $loggable {".indexOf("$"));
//		for (int i = 0; i < args.length; i++) {
//			CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
//		}
//		Kernel32 lib = (Kernel32) Native.loadLibrary ("kernel32",
//                Kernel32.class);
//		Kernel32.SystemTime time = new Kernel32.SystemTime();
//		lib.GetLocalTime(time);
//		System.out.println(time);
	}
}