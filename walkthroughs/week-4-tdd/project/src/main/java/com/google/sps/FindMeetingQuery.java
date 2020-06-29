// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
  
    long duration = request.getDuration();
    List<Event> eventList = new ArrayList<>(events);
    Collection<String> attendees = new HashSet<>(request.getAttendees());
    List<TimeRange> meetingTimes = new ArrayList<>();
    List<TimeRange> combinedTimes = new ArrayList<>();
    List<TimeRange> openTimes = new ArrayList<>();

    /**
    remove events that do not have conflicting attendees 
    with newly created meeting
    */
    for(int k = 0; k < eventList.size(); k++){
        Event event = eventList.get(k);
        Set<String> eventAttendees = new HashSet<>(event.getAttendees());
        eventAttendees.retainAll(attendees);
        if(eventAttendees.size() == 0){
            eventList.remove(k);
        }
    }

    /**
    create a list of all the meeting times
    note: a meeting cannot be scheduled during these time periods
    */
    for(Event event: eventList){
        meetingTimes.add(event.getWhen());
    }
    
    //sort meetings by earliest start time then end time
    meetingTimes.sort(Comparator.comparing(TimeRange::start)
                        .thenComparing(Comparator.comparing(TimeRange::end)));
    
    //combine overlapping time periods
    int k = 0;
    while(k < meetingTimes.size()){
        TimeRange current = meetingTimes.get(k);
        int m = k + 1;
        int start = current.start();
        int end = current.end();

        while(m < meetingTimes.size() && current.overlaps(meetingTimes.get(m))){
            if(meetingTimes.get(m).end() > end){
                end = meetingTimes.get(m).end();
            }
            current = TimeRange.fromStartEnd(start,end,false);
            m++;
        }

        combinedTimes.add(current);
        k = m;
    }

    //add open time slots to list
    int start = 0;
    TimeRange potentialSlot;
    for(int j = 0; j < combinedTimes.size(); j++){
        TimeRange current = combinedTimes.get(j);
        potentialSlot = TimeRange.fromStartEnd(start,current.start(),false);
        if(potentialSlot.duration() >= duration){
            openTimes.add(potentialSlot);
        }
        start = current.end();
    }

    //check time slot after last event
    potentialSlot = TimeRange.fromStartEnd(start,1440,false);
    if(potentialSlot.duration() >= duration){
        openTimes.add(potentialSlot);
    }
    return openTimes;
  }
}
