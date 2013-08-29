/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.hh.ghoststory.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

/** A button with a child {@link Label} to display text.
 * @author Nathan Sweet */
public class Text extends Widget {
	private TextStyle style;
	String text;

	public Text (String text, Skin skin) {
		this(text, skin.get(TextStyle.class));
	}

	public Text (String text, Skin skin, String styleName) {
		this(text, skin.get(styleName, TextStyle.class));
	}

	public Text (String text, TextStyle style) {
		super();
		setStyle(style);
		this.style = style;
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public void setStyle (TextStyle style) {
		if (!(style instanceof TextStyle)) throw new IllegalArgumentException("style must be a TextStyle.");
		this.style = (TextStyle)style;
	}

	public TextStyle getStyle () {
		return style;
	}

	public void draw (SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	public void setText (String text) {
//		label.setText(text);
	}

	public CharSequence getText () {
//		return label.getText();
		return "NOT READY YET";
	}

	/** The style for a text button, see {@link Text}.
	 * @author Nathan Sweet */
	static public class TextStyle {
		public BitmapFont font;
		/** Optional. */
		public Color fontColor, downFontColor, overFontColor, checkedFontColor, checkedOverFontColor, disabledFontColor;

		public TextStyle () {
		}

		public TextStyle (Drawable up, Drawable down, Drawable checked, BitmapFont font) {
			this.font = font;
		}

		public TextStyle (TextStyle style) {
			this.font = style.font;
			if (style.fontColor != null) this.fontColor = new Color(style.fontColor);
			if (style.downFontColor != null) this.downFontColor = new Color(style.downFontColor);
			if (style.overFontColor != null) this.overFontColor = new Color(style.overFontColor);
			if (style.checkedFontColor != null) this.checkedFontColor = new Color(style.checkedFontColor);
			if (style.checkedOverFontColor != null) this.checkedFontColor = new Color(style.checkedOverFontColor);
			if (style.disabledFontColor != null) this.disabledFontColor = new Color(style.disabledFontColor);
		}
	}
}

