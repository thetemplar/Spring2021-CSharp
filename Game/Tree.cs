using System;
using System.Collections.Generic;
using System.Text;

namespace CodingNetwork.Game
{
    class Tree
    {
        private int size;
        private Player owner;
        private int fatherIndex = -1;
        private bool isDormant;

        public int getFatherIndex()
        {
            return fatherIndex;
        }

        public void setFatherIndex(int fatherIndex)
        {
            this.fatherIndex = fatherIndex;
        }

        public Player getOwner()
        {
            return owner;
        }

        public void setOwner(Player owner)
        {
            this.owner = owner;
        }

        public int getSize()
        {
            return size;
        }

        public void setSize(int size)
        {
            this.size = size;
        }

        public void grow()
        {
            size++;
        }

        public bool isDormant()
        {
            return isDormant;
        }

        public void setDormant()
        {
            this.isDormant = true;
        }

        public void reset()
        {
            this.isDormant = false;
        }
    }
}
