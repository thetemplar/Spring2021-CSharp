using System;
using System.Collections.Generic;
using System.Text;

namespace CodingNetwork.Game
{
    class Cell
    {
        public static final Cell NO_CELL = new Cell(-1)
        {
            @Override
        public bool isValid()
            {
                return false;
            }

            @Override
        public int getIndex()
            {
                return -1;
            }
        };

        private int richness;
        public int index;

        public Cell(int index)
        {
            this.index = index;
        }

        public int getIndex()
        {
            return index;
        }

        public bool isValid()
        {
            return true;
        }

        public void setRichness(int richness)
        {
            this.richness = richness;
        }

        public int getRichness()
        {
            return richness;
        }
    }
}
