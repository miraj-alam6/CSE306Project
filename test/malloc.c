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
	//Check if size is less than or equal to 0 because this is invalid size request
	
	//Check if the heap has been started, If not extend heap_limit set firstfree to this then split the block if it's too big
	//return allocated memory data
	//set firstfree to the other part of the split block
	
	//else
	//Find a free block that is big enough to complete the malloc
	//If no free block big enough-> extend the heap_limit first
	
	//if block is big enough and bigger than what we need -> split the block into a free region and allocated region
	//update free list
	//if block used is firstfree have to update firstfree
	
	//else if block is just the right size we need then allocate it and update freelist
	//if block used is firstfree have to update firstfree
}

void free(void *ptr)
{
	//Check if pointer is valid
	
	//Free the pointer
	
	//Find the next freeblock closest to this
	//Find the prev freeblock closest to this
	//Add this block to freelist
	//If this block is right next to the end/start of any freeblock coalesce
	
}	