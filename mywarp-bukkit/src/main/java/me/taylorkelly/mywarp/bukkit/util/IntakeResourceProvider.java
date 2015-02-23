/*
 * Copyright (C) 2011 - 2015, MyWarp team and contributors
 *
 * This file is part of MyWarp.
 *
 * MyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyWarp. If not, see <http://www.gnu.org/licenses/>.
 */

package me.taylorkelly.mywarp.bukkit.util;

import com.sk89q.intake.util.i18n.ResourceProvider;

import me.taylorkelly.mywarp.util.i18n.LocaleManager;

import java.util.ResourceBundle;

/**
 * Provides internal resources to Intake.
 *
 * @see me.taylorkelly.mywarp.util.i18n.DynamicMessages
 */
public class IntakeResourceProvider implements ResourceProvider {

  private static final String RESOURCE_BUNDLE_NAME = "me.taylorkelly.mywarp.lang.Intake";

  private final ResourceBundle.Control control;

  /**
   * Creates an instance using the given ResourceBundle.Control to control the ResourceBundle loading.
   *
   * @param control the ResourceBundle.Control
   */
  public IntakeResourceProvider(ResourceBundle.Control control) {
    this.control = control;
  }

  @Override
  public ResourceBundle getBundle() {
    return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, LocaleManager.getLocale(), control);
  }
}
