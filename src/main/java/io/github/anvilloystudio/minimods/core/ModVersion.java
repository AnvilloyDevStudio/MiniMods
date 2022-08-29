package io.github.anvilloystudio.minimods.core;

import java.util.StringTokenizer;

import org.tinylog.Logger;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Referring to https://docs.minecraftforge.net/en/1.14.x/conventions/versioning/.
 * <p>{@code MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH}
 * <p>Actual format {@code MAJOR.MINOR.PATCH[-INDEV]}.
 * <p>For target minicraft version: mod meta "gameVersion".
 * For target loader version: mod meta "loaderVersion".
 */
public class ModVersion implements Comparable<ModVersion> {
    private int majorVersion;
    private int minorVersion;
    private int patchVersion;
    private int inDevVersion;
	private int inDevLevel;

	private static String[] inDevLevels = new String[] {"snapshot", "dev", "alpha", "beta", "pre", "rc"};

    public ModVersion(String version) throws MalformedModVersionFormatException {
        int index = version.indexOf("-");
        String part1;
        String part2 = null;

        if ( index < 0 ) {
            part1 = version;
        } else {
            part1 = version.substring( 0, index );
            part2 = version.substring( index + 1 );
        }

        StringTokenizer token = new StringTokenizer(part1, ".");
        try {
            majorVersion = Integer.parseInt(token.nextToken());
        } catch (NumberFormatException e) {
            throw new MalformedModVersionFormatException("Major Version Number", e);
        } if (token.hasMoreTokens()) try {
            minorVersion = Integer.parseInt(token.nextToken());
        } catch (NumberFormatException e) {
            throw new MalformedModVersionFormatException("Minor Version Number", e);
        } if (token.hasMoreTokens()) try {
            patchVersion = Integer.parseInt(token.nextToken());
        } catch (NumberFormatException e) {
            throw new MalformedModVersionFormatException("Patch Version Number", e);
        }

		if (part2 != null) {
			boolean exist = false;
            for (int i = 0; i < inDevLevels.length; i++) {
                String level = inDevLevels[i];
                if (part2.equals(level)) {
                    inDevVersion = 1;
					inDevLevel = i;
					exist = true;
					break;
                } else if (part2.startsWith(level)) {
					try {
						inDevVersion = Integer.parseInt(part2.substring(level.length()));
						if (inDevVersion == 0)
							Logger.warn("InDev version number 0 is ignored. No inDev instead. Source: {}", version);
						inDevLevel = i;
						exist = true;
						break;
					} catch (NumberFormatException e) {
						throw new MalformedModVersionFormatException("InDev Version Number: " + level, e);
					}
				}
            }

			if (!exist)
				throw new MalformedModVersionFormatException("Invalid inDev version " + part2 + " in " + version);
        }
    }

	public static class MalformedModVersionFormatException extends RuntimeException {
		public MalformedModVersionFormatException(String msg) { super(msg); }
		public MalformedModVersionFormatException(String msg, Throwable cause) { super(msg, cause); }
        public MalformedModVersionFormatException(Throwable cause) { super(cause); }
	}

    @Override
    public int hashCode()
    {
        return majorVersion * 0xFFD0 + minorVersion * 0xF765 + patchVersion * 0x762 + inDevLevel * inDevVersion * 0x88;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ModVersion)) {
            return false;
        }

        return compareTo((ModVersion) other) == 0;
    }

    @Override
    public int compareTo(ModVersion otherVersion)
    {
        if (otherVersion instanceof ModVersion) {
			if (majorVersion != otherVersion.majorVersion) return Integer.compare(majorVersion, otherVersion.majorVersion);
			if (minorVersion != otherVersion.minorVersion) return Integer.compare(minorVersion, otherVersion.minorVersion);
			if (patchVersion != otherVersion.patchVersion) return Integer.compare(patchVersion, otherVersion.patchVersion);
			if (inDevVersion != 0 || inDevVersion != 0) { // If inDev is valid.
				if (inDevVersion == 0) return 1; //0 is the last "dev" version, as it is not a dev.
				if (otherVersion.inDevVersion == 0) return -1;
				if (inDevLevel != otherVersion.inDevLevel) Integer.compare(inDevLevel, otherVersion.inDevLevel);
				return Integer.compare(inDevVersion, otherVersion.inDevVersion);
			}
			return 0; // The versions are equal.
		} else {
            return compareTo(new ModVersion(otherVersion.toString()));
        }
    }

    public int getMajorVersion() { return majorVersion; }
    public int getMinorVersion() { return minorVersion; }
    public int getPatchVersion() { return patchVersion; }
    public int getInDevVersion() { return inDevVersion; }
    public int getInDevLevel() { return inDevLevel; }
	public String getInDevLevelName() { return inDevLevels[inDevLevel]; }
	public boolean isInDev() { return inDevVersion != 0; }

    @Override
    public String toString() {
        return majorVersion + "." + minorVersion + "." + patchVersion + (inDevVersion != 0 ? "-" + inDevLevels[inDevLevel] + inDevVersion : "");
    }
}
