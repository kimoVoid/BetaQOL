package me.kimovoid.betaqol.command;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.command.exception.CommandException;
import me.kimovoid.betaqol.command.exception.InvalidNumberException;

public abstract class Command implements ICommand {

	public Command() {}

	public boolean requiresOp() {
		return true;
	}

	public static int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException var3) {
			throw new InvalidNumberException("'%s' is not a valid number", s);
		}
	}

	public static int parseInt(String s, int min) {
		return parseInt(s, min, 2147483647);
	}

	public static int parseInt(String s, int min, int max) {
		int var4 = parseInt(s);
		if (var4 < min) {
			throw new InvalidNumberException("The number you have entered (%d) is too small, it must be at least %d", var4, min);
		} else if (var4 > max) {
			throw new InvalidNumberException("The number you have entered (%d) is too big, it must be at most %d", var4, max);
		} else {
			return var4;
		}
	}

	public static double parseDouble(String s) {
		try {
			double var2 = Double.parseDouble(s);
			if (!Double.isFinite(var2)) {
				throw new InvalidNumberException("'%s' is not a valid number", s);
			} else {
				return var2;
			}
		} catch (NumberFormatException var4) {
			throw new InvalidNumberException("'%s' is not a valid number", s);
		}
	}

	public static double parseDouble(String s, double min) {
		return parseDouble(s, min, 1.7976931348623157E308D);
	}

	public static double parseDouble(String s, double min, double max) {
		double var6 = parseDouble(s);
		if (var6 < min) {
			throw new InvalidNumberException("The number you have entered (%.2f) is too small, it must be at least %.2f", var6, min);
		} else if (var6 > max) {
			throw new InvalidNumberException("The number you have entered (%.2f) is too big, it must be at most %.2f", var6, max);
		} else {
			return var6;
		}
	}

	public static boolean parseBoolean(String s) {
		if (!s.equals("true") && !s.equals("1")) {
			if (!s.equals("false") && !s.equals("0")) {
				throw new CommandException("'%s' is not true or false", s);
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public void sendSuccess(String player, String message) {
		String msg = player + ": " + message;
		BetaQOL.SERVER.playerManager.sendMessage("§7(" + msg + ")");
		BetaQOL.SERVER.sendMessage(msg);
	}

	public static double parseCoordinate(double c, String s) {
		return parseCoordinate(c, s, -30000000, 30000000);
	}

	public static double parseCoordinate(double c, String s, int min, int max) {
		boolean var6 = s.startsWith("~");
		if (var6 && Double.isNaN(c)) {
			throw new InvalidNumberException("'%s' is not a valid number", c);
		} else {
			double var7 = var6 ? c : 0.0D;
			if (!var6 || s.length() > 1) {
				boolean var9 = s.contains(".");
				if (var6) {
					s = s.substring(1);
				}

				var7 += parseDouble(s);
				if (!var9 && !var6) {
					var7 += 0.5D;
				}
			}

			if (min != 0 || max != 0) {
				if (var7 < (double)min) {
					throw new InvalidNumberException("The number you have entered (%.2f) is too small, it must be at least %.2f", var7, min);
				}

				if (var7 > (double)max) {
					throw new InvalidNumberException("The number you have entered (%.2f) is too big, it must be at most %.2f", var7, max);
				}
			}

			return var7;
		}
	}
}
