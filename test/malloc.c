#include <stdlib.h>
#include <stdio.h>
 
extern void *heap_start;
extern void *heap_limit;
static struct memory_region *firstfree;

struct memory_region {
           struct memory_region *next;
           int size;
		   char data[0];  /* The data starts here and continues on. */
        };

void *malloc(unsigned int size);
{
	
	if(size <= 0)
	{
		
	}
	
	if(*heap_start == *heap_limit)
	{
		//Extend heap_limit
		//*heap_limit += size;
		firstfree = (struct memory_region *)heap_start;
		firstfree->next = NULL;
		firstfree->size = heap_limit - heap_start;
		return firstfree;
	}
	else
	{
		struct memory_region *track = firstfree;
		
		while(track != NULL)
		{
			if(track->size >= size+sizeof(struct memory_region *))
			{
				break;
			}
			else
			{
				track = track->next;
			}
		}
		
		if(track == NULL)
		{
			//Extend heap_limit
			
		}
	
		struct memory_region *t2 = firstfree;
		while(t2->next != track)
		{
			t2 = t2->next;
		}
		
		if(track->size > size+sizeof(struct memory_region *))
		{
			struct memory_region *t3 = track;
			t3 += size+sizeof(struct memory_region *);
			t3->next = t2->next;
			t2->next = t3;
			return track;
		}
		else
		{
			track += size + sizeof(struct memory_region *);
			return track;
		}
		
	}
}

void free(void *ptr)
{
	struct memory_region *pp =
            (struct memory_region *)(p - ((void *)((struct memory_region *)p)->data - p));
			
	//if pp exists
	if(pp < heap_limit && pp > heap_start)
	{
		//Compare address to freelist
		struct memory_region *prevPP = firstfree;
		struct memory_region *nextPP = firstfree;
		
		if(pp < firstfree)
		{
			pp->next = firstfree->next;
			firstfree = pp;
			nextPP = pp->next;
		}
		else
		{
			//fit into correct spot by ascending order
			struct memory_region *curr = firstfree;
			
			while(curr != NULL)
			{
				if(pp < curr)
				{
					pp->next = curr;
					break;
				}
				curr = curr->next;
			}
			
			curr = firstfree;
			while(curr != NULL)
			{
				if(curr->next == pp->next)
				{
					break;
				}
				curr = curr->next;
			}
			
			curr->next = pp;
			
			//check if the end of prev is contigious with the current address
			//check if size+address of this to contingious of next address
		}	
	}
	//else
	else
	{
		//does not exist in the heap
	}
}	