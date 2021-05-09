using System;
using System.Collections.Generic;
using System.Text;

namespace CodingNetwork.Game
{
    class Player
    {
        private String message;
        private Action action;
        private int sun;
        private bool waiting = false;
        private int bonusScore = 0;

        public Player()
        {
            sun = Config.STARTING_SUN;
            action = Action.NO_ACTION;

        }

        @Override
    public int getExpectedOutputLines()
        {
            return 1;
        }

        public void addScore(int score)
        {
            setScore(getScore() + score);
        }

        public void reset()
        {
            message = null;
            action = Action.NO_ACTION;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public void setAction(Action action)
        {
            this.action = action;
        }

        public Action getAction()
        {
            return action;
        }

        public int getSun()
        {
            return sun;
        }

        public void setSun(int sun)
        {
            this.sun = sun;
        }

        public void addSun(int sun)
        {
            this.sun += sun;
        }

        public void removeSun(int amount)
        {
            this.sun = Math.max(0, this.sun - amount);
        }

        public bool isWaiting()
        {
            return waiting;
        }

        public void setWaiting(boolean waiting)
        {
            this.waiting = waiting;
        }

        public String getBonusScore()
        {
            if (bonusScore > 0)
            {
                return String.format(
                    "%d points and %d trees",
                    getScore() - bonusScore,
                    bonusScore
                );
            }
            else
            {
                return "";
            }
        }

        public void addBonusScore(int bonusScore)
        {
            this.bonusScore += bonusScore;
        }
    }
}
