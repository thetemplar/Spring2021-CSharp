using System;
using System.Collections.Generic;
using System.Text;

namespace CodingNetwork.Game
{
    public enum FrameType
    {
        GATHERING,
        ACTIONS,
        SUN_MOVE,
        INIT
    }

    class Constants
    {
        public static int RICHNESS_NULL = 0;
        public static int RICHNESS_POOR = 1;
        public static int RICHNESS_OK = 2;
        public static int RICHNESS_LUSH = 3;

        public static int TREE_SEED = 0;
        public static int TREE_SMALL = 1;
        public static int TREE_MEDIUM = 2;
        public static int TREE_TALL = 3;

        public static int[] TREE_BASE_COST = new int[] { 0, 1, 3, 7 };
        public static int TREE_COST_SCALE = 1;
        public static int LIFECYCLE_END_COST = 4;
        public static int DURATION_ACTION_PHASE = 1000;
        public static int DURATION_GATHER_PHASE = 2000;
        public static int DURATION_SUNMOVE_PHASE = 1000;
        public static int STARTING_TREE_COUNT = 2;
        public static int RICHNESS_BONUS_OK = 2;
        public static int RICHNESS_BONUS_LUSH = 4;
    }
}
