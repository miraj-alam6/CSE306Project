#include "malloc2.h"

extern void *heap_start;
extern void *heap_limit;

static struct memory_region *firstfree;

struct memory_region {
           struct memory_region *next;
           int size;
		   char data[0];  /* The data starts here and continues on. */
        };        
void *malloc(unsigned int size){
	struct memory_region *prev;
    struct memory_region *curr;
    struct memory_region * next;
    struct memory_region *newEntry;
    int foundRegion = 0;
	int oldSize = 0;
	/*Check if size is less than or equal to 0 because this is invalid size request*/
	if(size <= 0)
	{
		return NULL;
	}
	/*Word align the size of the data region, this should happen every single time you call malloc*/
	if(size % 4 != 0){
		size = size + (4 - (size %4));
		
	}	
	/*Check word alignment, this only happens once AKA, the first time*/
	if((int)heap_start % 4 != 0){
		heap_start = heap_start + (4 - (int)heap_start %4);
		/* if this is still the first time, which is most likely is, because heap start can never change,
		also align heap_limit. Just in case it isn't the first time, you should not set the heap_limit here,
		thus this if statement exists*/
		if(heap_limit < heap_start){
			heap_limit = heap_start;
		}
	}
	/*This is also just for the first time*/
	if(heap_limit == heap_start)
	{
		/*extend heap limit*/
		heap_limit += 128;
		firstfree = (struct memory_region *)heap_start;
        firstfree->next = NULL;
        firstfree->size = heap_limit - heap_start;	
	}
	prev = NULL;
	next = NULL;
	curr = firstfree;
	while(curr != NULL){
		next = curr-> next;
		
		/*This is when you find a valid entry*/
		if(curr-> size > size){
			oldSize = curr->size;
			foundRegion = 1;
			newEntry = curr;
			newEntry->size = size;
			curr = curr + size* sizeof(char) + sizeof(int) + sizeof(void*);
			/*TODO: this region may be problematic*/
			curr->size = oldSize - (size * sizeof(char) + sizeof(int) + sizeof(void*));
			curr->next = newEntry->next;
			newEntry->next = NULL;
			/*if first free region has it*/
			if(prev == NULL){		
				firstfree = curr;
				return newEntry;
			}
			/*If a region in the middle has it*/
			else{
				prev->next = curr;
				return newEntry;
			}
		}
		/*if you'v reached the end, you need to maintain both prev and curr, in order to extend and allocate and keep the
		linked list intact.*/
		if(curr->next == NULL){
			break;
		}
		else{
			prev = curr;
			curr = next;
		}
	}
	
	/*curr is now the last region because of the while loop*/
	/*curr should not be null, thus if it is, return null*/
	if(curr == NULL){
		return NULL; 
	}
	
	if(foundRegion == 0){
		heap_limit += 128;
		curr->size += 128;
		while((int)curr - (int)heap_limit < size){
			heap_limit += 128;
			curr->size += 128;
		}
	}
	
	oldSize = curr->size;
	foundRegion = 1;
	newEntry = curr;
	newEntry->size = size;
	curr = curr + size* sizeof(char) + sizeof(int) + sizeof(void*);
	/*probably to do*/
	curr->size = oldSize - size * sizeof(char) + sizeof(int) + sizeof(void*);
	curr->next = newEntry->next;
	newEntry->next = NULL;
	prev->next = curr;
	return newEntry;
}

void joinRegions(struct memory_region *prev, struct memory_region *curr, struct memory_region *next){
    /*Front of list*/
    if(prev == NULL){
        if(next == NULL){
            return;
        }
        if(curr + sizeof(void*) +sizeof(int) + curr->size * sizeof(char) == next){
            curr->size = curr->size + next->size;
            curr->next = next->next;    
        }
    }
    else{
        /*I think we can assume curr will not be null, because free() explictly creates what it passes into this parameter*/
        if(prev + sizeof(void*) +sizeof(int) + prev->size * sizeof(char) == curr ){
            prev->size = prev->size + curr->size;
            prev -> next = curr->next;
            /*Now we have only two regions: prev and next. prev now entails curr as well. So now see if these two should join*/
            if(next == NULL){
                return;
            }
            else if(prev + sizeof(void*) +sizeof(int) + prev->size * sizeof(char) == next ){
                prev->size = prev->size + next->size;
                prev -> next = next->next;
            }
        }

        else{
            if(next == NULL){
                return;
            }
            if(curr + sizeof(void*) +sizeof(int) + curr->size * sizeof(char) == next){
                curr->size = curr->size + next->size;
                curr->next = next->next;
            }
        }
    }

}
	
void free (void *target){
    struct memory_region *prev;
    struct memory_region *curr;
    struct memory_region * next;
    struct memory_region *newEntry = target - sizeof(int) - sizeof(void *);
    int foundRegion = 0;
    /* The following two are already set I think implicitly after after setting
    new entry two lines of code ago. I tried doing the next to lines of
    code, but no matter how I was casting, the things weren't working.
    
    newEntry->data = (char[])target;
    newEntry->size = *(target - sizeof(int));
    */
    newEntry->next = NULL;

    if(firstfree == NULL){
        firstfree = newEntry;
    }

    else{
        /*Place this new free region in the correct place*/
        prev = NULL;
        curr = firstfree;
        next = NULL;
        while(curr != NULL){
            next = curr -> next;    

            if(newEntry < curr){
                foundRegion = 1;
                newEntry->next = curr;
                if(prev == NULL){
                    firstfree = newEntry;
                    break;
                }
                else{
                    prev-> next = newEntry;
                    break;
                }
            }
            prev = curr;
            curr = next;
        }
        /*If we couldn't find it by looping through. We need to put it at the end. The last entry is stored in prev*/
        if(foundRegion == 0){
            foundRegion = 1;
            prev-> next = newEntry;
        }

        joinRegions(prev, newEntry, newEntry->next);

    }
    
}
