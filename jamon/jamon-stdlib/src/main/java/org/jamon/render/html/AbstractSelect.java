/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.jamon.render.html;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated public abstract class AbstractSelect<Renderable>
    extends AbstractInput
    implements Select<Renderable>
{

    @SuppressWarnings({ "hiding", "deprecation", "unchecked" })
    public abstract static class Item<Renderable>
        implements Select.Item<Renderable>
    {
        public final String getName()
        {
            return m_select.getName();
        }
        protected AbstractSelect getSelect()
        {
            return m_select;
        }
        private void setSelect(AbstractSelect p_select)
        {
            m_select = p_select;
        }
        private AbstractSelect m_select;
    }

    public Item<? extends Renderable>[] getItems()
    {
        return m_items.clone();
    }


    @SuppressWarnings({"hiding", "deprecation"})
    @Deprecated
    public interface ItemMaker<DataType, Renderable>
    {
        Select.Item<Renderable> makeItem(DataType p_data);
    }

    protected <DataType> AbstractSelect(
        String p_name,
        DataType[] p_data,
        ItemMaker<? super DataType, Renderable> p_maker)
    {
        this(p_name, Arrays.asList(p_data).iterator(), p_maker);
    }

    protected <DataType> AbstractSelect(
        String p_name,
        Iterator<? extends DataType> p_data,
        ItemMaker<? super DataType, Renderable> p_maker)
    {
        this(p_name, create(p_data, p_maker));
    }

    protected AbstractSelect(String p_name,
                             Item<? extends Renderable>[] p_items)
    {
        super(p_name);
        m_items = p_items;
        for(int i = 0; i < m_items.length; ++i)
        {
            m_items[i].setSelect(this);
        }
    }

    @SuppressWarnings("unchecked")
    private static<Renderable, DataType> Item<? extends Renderable>[] create(
       Iterator<? extends DataType> p_data,
       ItemMaker<DataType, Renderable> p_maker)
    {
        List<Select.Item> items = new ArrayList<Select.Item>();
        while( p_data.hasNext() )
        {
            items.add(p_maker.makeItem( p_data.next()));
        }
        return items.toArray(new Item[items.size()]);
    }

    private final Item<? extends Renderable>[] m_items;
}
