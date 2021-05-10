using System;
using System.Collections.Generic;
using System.Text;

namespace CodingNetwork.Game
{
    class Board
    {
        public Dictionary<CubeCoord, Cell> map;
        public List<CubeCoord> coords;

        public Board(Dictionary<CubeCoord, Cell> map)
        {
            this.map = map;
            coords = map.entrySet()
                .stream()
                .sorted(
                    (a, b)->a.getValue().getIndex() - b.getValue().getIndex()
                )
                .map(Entry::getKey)
                .collect(Collectors.toList());
        }
    }
}
