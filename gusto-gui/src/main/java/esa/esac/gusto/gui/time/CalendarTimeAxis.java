/*
 * This file is part of Herschel Common Science System (HCSS).
 * Copyright 2001-2016 Herschel Science Ground Segment Consortium
 *
 * HCSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * HCSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with HCSS.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package esa.esac.gusto.gui.time;

import esa.esac.gusto.constraint.TimeInterval;
import esa.esac.gusto.gui.time.CalendarTimeAxis.Tick;
import esa.esac.gusto.time.TaiTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Generates axis tick marks and labels for a UTC time axis.
 * 
 * @author  Jon Brumfitt
 */
public class CalendarTimeAxis implements Iterable<Tick> {
    // FIXME: This implementation does not generate a tick for second 
    // number "60" at leap seconds.
    
    private static final int DAY = 86400;
    private static final int HOUR = 3600;
    private static final int MINUTE = 60;
    
    // Thresholds at which ranging changes (in seconds)
    private static final int t01 = 2 * MINUTE;
    private static final int t02 = 10 * MINUTE;
    private static final int t03 = 1 * HOUR;
    private static final int t04 = 2 * HOUR;
    private static final int t05 = 10 * HOUR;
    private static final int t06 = 2 * DAY;
    private static final int t07 = 10 * DAY;
    private static final int t08 = 60 * DAY;
    private static final int t09 = 180 * DAY;
    private static final int t10 = 300 * DAY;
    private static final int t11 = 1500 * DAY;
    
    private TimeInterval _timeRange;
    private List<Tick> _ticks;
    private GregorianCalendar _calendar;
    
    /**
     * Create a new UtcTimeAxis.
     */
    public CalendarTimeAxis(TimeInterval range) {
	_timeRange = range;
	_ticks = new ArrayList<Tick>();
	_calendar = new GregorianCalendar();
	_calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
	
	generate();
    }
    
    /**
     * Set the time range of the axis.
     */
    public void setTimeRange(TimeInterval range) {
	if(range != _timeRange) {
	    _timeRange = range;
	    generate();
	}
    }
    
    /**
     * An axis tick mark.
     */
    public class Tick {
	private TaiTime _time;
	private String _label;
	private boolean _isMajor;
	
	Tick(TaiTime time, String label, boolean major) {
	    _time = time;
	    _label = label;
	    _isMajor = major;
	}
	
	public TaiTime getTime() {
	    return _time;
	}
	
	public String getLabel() {
	    return _label;
	}
	
	public boolean isMajor() {
	    return _isMajor;
	}
    }
    
    /**
     * Return an iterator over the ticks.
     */
    public Iterator<Tick> iterator() {
	return _ticks.iterator();
    }
    
    /**
     * Return the time of the next tick.
     */
    private TaiTime increment(long period) {
	if(period < t01) {
	    _calendar.add(Calendar.SECOND, 1);
	} else if(period < t02) {
	    _calendar.add(Calendar.SECOND, 10);
	} else if(period < t04) {
	    _calendar.add(Calendar.MINUTE, 1);
	} else if(period < t05) {
	    _calendar.add(Calendar.MINUTE, 10);
	} else if(period < t06) {
	    _calendar.add(Calendar.HOUR_OF_DAY, 1);
	} else if(period < t07) {
	    _calendar.add(Calendar.HOUR_OF_DAY, 6);    
	}  else if(period < t08) {
	    _calendar.add(Calendar.DAY_OF_MONTH, 1);
	}  else if(period < t09) {
	    _calendar.add(Calendar.DAY_OF_MONTH, 1);
	} else if(period < t11) {
	    _calendar.add(Calendar.MONTH, 1);
	} else {
	    _calendar.add(Calendar.YEAR, 1);
	}
	return new TaiTime(_calendar.getTime());
    }
    
    /**
     * Initialize calendar before the first tick.
     */
    private void reset(TaiTime start) {
	_calendar.setTime(start.toDate());
	long period = _timeRange.duration() / 1000000L;
	
	_calendar.set(Calendar.MILLISECOND, 0);
	if(period >= t01) {
	    _calendar.set(Calendar.SECOND, 0);
	}
	if(period >= t04) {
	    _calendar.set(Calendar.MINUTE, 0);
	}
	if(period >= t06) {
	    _calendar.set(Calendar.HOUR_OF_DAY, 0);
	}
	if(period >= t09) {
	    _calendar.set(Calendar.DAY_OF_MONTH, 1);
	}
	if(period >= t11) {
	    _calendar.set(Calendar.MONTH, 0);
	}
    }
    
    /**
     * Generate and cache the ticks.
     */
    private void generate() {
	_ticks.clear();
	
	TaiTime tStart = _timeRange.start();
	TaiTime tEnd   = _timeRange.finish();
	long period   = _timeRange.duration() / 1000000L;

	reset(tStart);
	TaiTime t = increment(period);
	int previous = 0;
	int value = 0;

	while(t.before(tEnd)) {
	    String label = "";
	    int sec = _calendar.get(Calendar.SECOND);
	    int min = _calendar.get(Calendar.MINUTE);
	    int hour = _calendar.get(Calendar.HOUR_OF_DAY);
	    int day = _calendar.get(Calendar.DAY_OF_MONTH);
	    int month = _calendar.get(Calendar.MONTH);
	    int year = _calendar.get(Calendar.YEAR);
	    
	    if(period < t01) {
		value = sec;
		if(sec == 0) {
		    label = String.format("%02d:%02d:%02d", hour, min, sec);
		} else if((sec % 10) == 0) {
		    label = "" + sec;
		    value = sec % 10;
		}
	    } else if(period < t02) {
		value = sec;
		if(sec == 0) {
		    label = String.format("%02d:%02d", hour, min);
		}		
	    } else if(period < t03) {
		value = min % 5;
		if(min % 5 == 0) {
		    label = String.format("%02d:%02d", hour, min);
		}		
	    } else if(period < t04) {
		value = min % 10;
		if(min % 10 == 0) {
		    label = String.format("%02d:%02d", hour, min);
		}		
	    } else if(period < t05) {
		value = min;
		if(min == 0) {
		    label = String.format("%02d:%02d", hour, min);
		}
	    } else if(period < t06) {
		value = hour % 6;
		if(hour % 6 == 0) {
		    label = String.format("%02d:%02d", hour, min);
		}
	    } else if(period < t07) {
		value = hour;
		if(hour == 0) {
		    label = day + "-" + _calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
//		    label = String.format("%04d-%02d-%02d", year, month+1, day);
		}
	    } else if(period < t08) {
		value = day;
		if(day == 1) {
		    label = day + "-" + _calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
//		    label = String.format("%04d-%02d-%02d", year, month+1, day);
		} else if(day % 7 == 1 && day < 29) {
		    label = "" + day;
		}
	    } else if(period < t10) {
		value = day;
		if(day == 1) {
		    label = _calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
//		    label = String.format("%04d-%02d", year, month+1);
		}
	    } else if(period < t11) {
		value = month;
		if(month == 0) {
		    label = "" + year;
		}
	    } else {
		value = year % 10;
		label = "" + year;
	    }
	    
	    boolean wrap = value <= previous;
	    _ticks.add(new Tick(t, label, wrap));
	    
	    previous = value;
	    t = increment(period);
	}
    }
}
