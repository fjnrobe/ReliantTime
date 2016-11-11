package common;

public class Year implements Comparable<Year> {

		private String sortKey;
		private String yearName;
		private int year;
		
		/**
		 * @return the sortKey
		 */
		public String getSortKey() {
			return sortKey;
		}
		/**
		 * @param sortKey the sortKey to set
		 */
		public void setSortKey(String sortKey) {
			this.sortKey = sortKey;
		}
		
		/**
		 * @return the yearName
		 */
		public String getYearName() {
			return yearName;
		}
		
		public int getYear()
		{
			return year;
		}		
		
		/**
		 * @param yearName the yearName to set
		 */
		public void setYearName(String yearName) {
			this.yearName = yearName;
		}
		
		/**
		 * @param year the year to set
		 */
		public void setYear(int year) {
			this.year = year;
		}
		
		@Override
		public int compareTo(Year another) {
			
			int compareValue = 0;
			
			if (this.year < another.getYear())
			{
				compareValue = -1;
			}
			else if (this.year > another.getYear())
			{
				compareValue = 1;
			}
			
			return compareValue;
		}

}
